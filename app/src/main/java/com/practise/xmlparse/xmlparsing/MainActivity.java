package com.practise.xmlparse.xmlparsing;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    Button parseBtn;
    private final String TAG="PARSER";
    private final String fileName="";
    private final String filePath="";
    private int filePriority;
    private  Context context=null;
    private FileParameterPOJO fileInformation;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        parseBtn=(Button)findViewById(R.id.parse_button);

        parseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    context=MainActivity.this;
                    ComparisonParameters parametersListAssets=new ComparisonParameters();
                    loadXml(parametersListAssets);
                    InputStream inputStreamAssets = context.getResources().getAssets().open("LogCodes.xml");
                    parseXMLFile(null,inputStreamAssets,parametersListAssets);

                }
                catch(Exception ex)
                {
                    Log.e(TAG,ex.getStackTrace().toString());
                }
            }
        });
    }

    private void loadXml(ComparisonParameters parametersListAssets)
    {
         /*fileInformation = new FileParameterPOJO(fileName,filePath,filePriority,fileName,filePath);


        File xmlFileDirectory  = new File(fileInformation.getSdCardFilePath());
        if(!xmlFileDirectory.exists()){
            xmlFileDirectory.mkdirs();
        }
        File xmlFile = new File(xmlFileDirectory, fileInformation.getFileName());

        if (!xmlFile.exists())
        {
            copyFileSdCard();
        }
        else
        {
            ComparisonParameters parametersListSdCard=new ComparisonParameters();
            ComparisonParameters parametersListAssets=new ComparisonParameters();


        }*/


        addXmlElementsToList(parametersListAssets, "fastLogcodes;version", 3, ComparisonConstants.ROOT_ELEMENT);


    }

    //Function to add Root element,Key element,Inner elements to the corresponding list
    private void addXmlElementsToList(ComparisonParameters comparisonParameters, String elementName,
                                      int mode, int elementType)
    {
        switch (elementType) {
            case ComparisonConstants.ROOT_ELEMENT:
                RootElementPOJO rootElement = new RootElementPOJO(elementName, null, mode);
                comparisonParameters.addRootParameter(rootElement);
                break;


            case ComparisonConstants.KEY_ELEMENT:

                break;

            case ComparisonConstants.INNER_ELEMENT:

                break;

        }

    }


    private void copyFileSdCard()
    {
        Runnable parseRunnable=new Runnable() {
            @Override
            public void run() {

                File xmlFileDirectory  = new File(fileInformation.getFinalFilePath(),fileInformation.getFinalFileName());

                try {
                    InputStream inputStream = context.getResources().getAssets().open(fileInformation.getFileName());
                    OutputStream outputStream=new FileOutputStream(xmlFileDirectory);

                    byte[] buffer = new byte[1024];
                    int read;
                    while ((read = inputStream.read(buffer)) != -1) {
                        outputStream.write(buffer, 0, read);
                    }
                    inputStream.close();
                    outputStream.flush();
                    outputStream.close();

                }
                catch(FileNotFoundException ex)
                {
                    ex.printStackTrace();
                    return;
                }
                catch(IOException ex)
                {
                    ex.printStackTrace();
                    return;
                }


            }
        } ;


    }


    private void parseXMLFile(InputStream sdCardFile,InputStream assetFile,
                                                  ComparisonParameters comparisonParameters)
            throws XmlPullParserException,IOException
    {
        XmlPullParserFactory xmlFactory=XmlPullParserFactory.newInstance();
        XmlPullParser xmlParser1=xmlFactory.newPullParser();
      //  XmlPullParser xmlParser2=xmlFactory.newPullParser();

        xmlParser1.setInput(assetFile,null);
       // xmlParser2.setInput(assetFile,null);

        int eventType1=xmlParser1.getEventType();
        //int eventType2=xmlParser1.getEventType();

        while (eventType1!=XmlPullParser.END_DOCUMENT)
        {
            switch(eventType1)
            {
                case XmlPullParser.START_DOCUMENT :
                        break;

                case XmlPullParser.START_TAG:

                    //This will give the element Tag name
                    String ElementName=xmlParser1.getName();

                    //---- --------------------Section handles Root Element Start-------------------
                    //Fetch the list containing root element attributes
                    List<RootElementPOJO> rootElementList=comparisonParameters.getRootParameterList();

                    /*Iterate over the list to check if current tag name is same as that
                    * stored in root element list*/
                    if(rootElementList!=null) {

                        for (int i=0;i<rootElementList.size();i++)
                        {
                            RootElementPOJO rootElementPOJO = rootElementList.get(i);

                            //The 0th index contains root element name and 1st index attribute name
                            String []splitName=rootElementPOJO.getElementName().split(";");

                            //If root element name is same as that of current tag
                            if(ElementName.equals(splitName[0]))
                            {
                                Log.d(TAG,"Root element Tag name "+splitName[0]);
                                Log.d(TAG,"Root element attribute name "+splitName[1]);

                                //get the attribute value stored at 1st index
                               String attributeValue=xmlParser1.getAttributeValue(null,splitName[1]);
                                Log.d(TAG,"Root element attribute Value "+attributeValue);
                                rootElementPOJO.setElementValue(attributeValue);

                                Log.d(TAG,"Root elements "+
                                        "Name:"+rootElementPOJO.getElementName()+
                                        " Value:"+rootElementPOJO.getElementValue()+
                                        " Mode:"+rootElementPOJO.getModeOfComparison());
                            }

                        }
                    }
                    //---- --------------------Section handles Root Element End---------------------

                    break;


                case XmlPullParser.TEXT:
                    //Log.i(TAG,xmlParser1.getText());
                    break;

                case XmlPullParser.END_TAG:
                    //Log.i(TAG,"/<"+xmlParser1.getName()+">"); // Returns the name of tag element
                    break;

            }

            eventType1=xmlParser1.next();
        }

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
