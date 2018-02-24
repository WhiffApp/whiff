package edu.sim.whiff.PcapWriter;

import java.io.IOException;


/**
 * Factory class for creating capture file reader.
 * The file type is determined by the file name prefix.
 * If the file prefix don't match its prefix the behaviour is unexpected.
 * 
 * @author Yeo Pei Xuan
 *
 */
public class CaptureFileFactory
{
	/**
	 * Create reader for capture file.
	 * @param theFileName
	 * @return CaptureFileReader instance.
	 * @throws IOException
	 * @throws NetUtilsException
	 */
	public static CaptureFileReader createCaptureFileReader(String theFileName) throws IOException, NetUtilsException
	{
		if (theFileName.toLowerCase().endsWith("cap")
				|| theFileName.toLowerCase().endsWith("pcap"))
		{
			return new PCapFileReader(theFileName);
		}
		
		throw new NetUtilsException("Capture Format not supported");
	}
	
	/**
	 * create writer for capture file.
	 * @param theFileName
	 * @return CaptureFileWriter instance
	 * @throws IOException
	 * @throws NetUtilsException
	 */
	public static CaptureFileWriter createCaptureFileWriter(String theFileName) throws IOException, NetUtilsException
	{
		if(theFileName.toLowerCase().endsWith("cap")
				|| theFileName.toLowerCase().endsWith("pcap"))
		{
			return new PCapFileWriter(theFileName);
		}
		
		throw new NetUtilsException("Capture Format not supported");
	}
	
	
	public static CaptureFileReader tryToCreateCaprtueFileReader(String theFileName) throws NetUtilsException
	{
		 
		try
		{
			PCapFileReader prd = new PCapFileReader(theFileName);
			if (prd.isValid())
				return prd;
		} catch (IOException e)
		{
			// TODO Auto-generated catch block
			//e.printStackTrace();
		}
		throw new NetUtilsException("Capture Format not supported");

	}
}
