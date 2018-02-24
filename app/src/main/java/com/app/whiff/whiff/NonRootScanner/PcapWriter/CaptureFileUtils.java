package com.app.whiff.whiff.NonRootScanner.PcapWriter;

import java.io.IOException;
import java.util.ArrayList;


/**
 * Collection of useful capture file processing functions.
 * 
 * 
 * @author Yeo Pei Xuan
 *
 */
public class CaptureFileUtils
{
	/**
	 * Read entire capture file into memory
	 * @param theFileName - the file name
	 * @return - array of packets (byte[][])
	 * @throws IOException
	 * @throws NetUtilsException
	 */
	public static byte[][] readCapRawData(String theFileName) throws IOException, NetUtilsException
	{
		ArrayList<byte[]> pktsArrList = new ArrayList<byte[]>();
		CaptureFileReader rd = CaptureFileFactory.createCaptureFileReader(theFileName);
		byte next[] = null;
		
		while( (next = rd.ReadNextPacket()) != null)
		{
			pktsArrList.add(next);
		}
		
		return pktsArrList.toArray(new byte[pktsArrList.size()][]);
	}
}
