package com.ivanchimeno.gympuplan.lib;

import android.util.Xml;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.io.InputStream;

public class GympuXmlParser
{

    public GympuResponse Parse(InputStream inputStream) throws XmlPullParserException, IOException
    {
        try
        {
            XmlPullParser parser = Xml.newPullParser();

            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(inputStream, null);
            parser.nextTag();
            return ReadResponse(parser);
        }
        finally
        {
            inputStream.close();
        }
    }

    private GympuResponse ReadResponse(XmlPullParser parser) throws XmlPullParserException, IOException
    {
        GympuResponse gympuResponse = new GympuResponse();

        parser.require(XmlPullParser.START_TAG, null, "GymPuLan");
        while (parser.next() != XmlPullParser.END_TAG)
        {
            if (parser.getEventType() != XmlPullParser.START_TAG)
                continue;

            String name = parser.getName();

            // Look for tags.
            if (name.equals("GymPuLanAction"))
                gympuResponse.setActionSent(ReadTagValue(parser, "GymPuLanAction"));

            if (name.equals("GymPuLanStatus"))
                gympuResponse.setStatusCode(ReadTagValue(parser, "GymPuLanStatus"));

            if (name.equals("GymPuLanUserName"))
                gympuResponse.setGympuLanUsername(ReadTagValue(parser, "GymPuLanUserName"));

            if (name.equals("GymPuLanDisplayName"))
                gympuResponse.setGympuLanDisplayName(ReadTagValue(parser, "GymPuLanDisplayName"));

            if (name.equals("GymPuLanUserGrade"))
                gympuResponse.setGympuLanUserGrade(ReadTagValue(parser, "GymPuLanUserGrade"));

            if (name.equals("GymPuLanUserGroup"))
                gympuResponse.setGympuLanUserGroup(ReadTagValue(parser, "GymPuLanUserGroup"));

            if (name.equals("GymPuLanSessionID"))
                gympuResponse.setGympuLanSessionId(ReadTagValue(parser, "GymPuLanSessionID"));

            if (name.equals("GymPuLanVPlanId"))
                gympuResponse.setGympuLanVPlanId(ReadTagValue(parser, "GymPuLanVPlanId"));

            if (name.equals("GymPuLanVPlanLastUpdate"))
                gympuResponse.setGympuLanVPlanLastUpdate(ReadTagValue(parser, "GymPuLanVPlanLastUpdate"));

            if (name.equals("GymPuLanVPlanNumberOfPages"))
                gympuResponse.setGympuLanVPlanNumberOfPages(ReadTagValue(parser, "GymPuLanVPlanNumberOfPages"));
        }

        return gympuResponse;
    }

    private String ReadTagValue(XmlPullParser parser, String tagName) throws XmlPullParserException, IOException
    {
        parser.require(XmlPullParser.START_TAG, null, tagName);
        String tagValue = ReadText(parser);
        parser.require(XmlPullParser.END_TAG, null, tagName);
        return tagValue;
    }

    private String ReadText(XmlPullParser parser) throws IOException, XmlPullParserException
    {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT)
        {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }
}
