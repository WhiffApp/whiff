package com.app.whiff.whiff.NonRootScanner.PcapWriter;

/**
 * class for holding complete libpcap block.<br>
 * Libpcap block is built of header and data.<br>
 * 
 * @author Yeo Pei Xuan
 *
 */
public class PCapBlock
{
	
	private PCapPacketHeader myPktHdr = null;
	private byte[] myData = null;
	
	/**
	 * Build Block.
	 * @param pktHdr
	 * @param data
	 */
	public PCapBlock(PCapPacketHeader pktHdr, byte[] data)
	{
		super();
		this.myPktHdr = pktHdr;
		this.myData = data;
	}

	/**
	 * 
	 * @return the packet PCapPacketHeader
	 */
	public PCapPacketHeader getMyPktHdr()
	{
		return myPktHdr;
	}

	/**
	 * 
	 * @return the packet as byte array.
	 */
	public byte[] getMyData()
	{
		return myData;
	}
}
