package com.wks.calorieapp.daos;

import java.text.ParseException;
import java.util.List;

public interface DataAccessObject<T extends Object>
{
	public long create(T object);
	public T read(long id) throws ParseException;
	public List<T> read() throws ParseException;
	public int update(T object);
	public int delete(long id);
	
}
