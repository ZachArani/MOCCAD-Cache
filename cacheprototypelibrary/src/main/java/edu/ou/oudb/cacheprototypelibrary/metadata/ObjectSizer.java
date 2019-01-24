package edu.ou.oudb.cacheprototypelibrary.metadata;


public class ObjectSizer {

	public static long getObjectSize32bits()
	{
		return 2*4;
	}
	
	public static long getObjectSize64bits()
	{
		return 2*8;
	}
	
	public static long getStringSize32bits(int stringLength)
	{
		return stringLength + 4 + 4 + 4;
	}
	
	public static long getStringSize64bits(int stringLength)
	{
		return stringLength + 8 + 4 + 4;
	}
	
	public static long getArrayOfObjectSize32bits(int arrayLength)
	{
		return 2*4 + 4 + 4 * arrayLength;
	}
	
	public static long getArrayOfObjectSize64bits(int arrayLength)
	{
		return 2*8 + 4 + 4 + 8 * arrayLength;
	}
}
