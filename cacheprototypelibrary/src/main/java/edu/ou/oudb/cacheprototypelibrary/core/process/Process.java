package edu.ou.oudb.cacheprototypelibrary.core.process;

import java.net.ConnectException;

import edu.ou.oudb.cacheprototypelibrary.querycache.exception.DownloadDataException;
import edu.ou.oudb.cacheprototypelibrary.querycache.exception.JSONParserException;

import android.support.annotation.Nullable;

public interface Process<K,T> {
	
	@Nullable
	public T run(K key) throws ConnectException, DownloadDataException, JSONParserException;

}
