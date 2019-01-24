package edu.ou.oudb.cacheprototypelibrary.connection;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.ou.oudb.cacheprototypelibrary.estimationcache.Estimation;
import edu.ou.oudb.cacheprototypelibrary.querycache.query.Query;
import edu.ou.oudb.cacheprototypelibrary.querycache.query.QuerySegment;

public class StubDataAccessProvider implements DataAccessProvider{

	private static final String RELATION = "NOTE";
	
	
	@Override
	public List<String> getAttributeTypes(String relation) {
		List<String> attributeTypes = new ArrayList<String>();
		
		switch (relation) {
		case RELATION:
			attributeTypes.add("NUMBER");
			attributeTypes.add("VARCHAR2");
			attributeTypes.add("VARCHAR2");
			attributeTypes.add("VARCHAR2");
			attributeTypes.add("DATE");
			attributeTypes.add("DATE");
			attributeTypes.add("NUMBER");
			break;
		}
		
		return attributeTypes;
	}

	@Override
	public List<String> getAttributeNames(String relation) {
		List<String> attributeNames = new ArrayList<String>();
		
		switch (relation) {
		case RELATION:
			attributeNames.add("NoteID");
			attributeNames.add("PatientName");
			attributeNames.add("DoctorName");
			attributeNames.add("Description");
			attributeNames.add("Date");
			attributeNames.add("Time");
			attributeNames.add("HeartRate");
			break;
		}
		
		return attributeNames;
	}

	@Override
	public long getNbTuples(String relation) {
		return 10;
	}

	@Override
	public int getNbAttributes(String relation) {
		return 7;
	}

	@Override
	public QuerySegment process(Query query) {
		QuerySegment segment = new QuerySegment();
		
		List<List<String>> tuples = new ArrayList<List<String>>();
		tuples.add(new ArrayList<String>(Arrays.asList(new String[]{"1","Paul Dupont","Darren Ben","Checkup","9/11/2014","9:30am","56"})));
		tuples.add(new ArrayList<String>(Arrays.asList(new String[]{"2","Jack Tristen","Bernard Clayton","Cardiac test","9/12/2014","10:30am","68"})));
		tuples.add(new ArrayList<String>(Arrays.asList(new String[]{"3","Manny Kingsley","John Smith","Anxiety disorder research","9/12/2014","1:45pm","80"})));
		tuples.add(new ArrayList<String>(Arrays.asList(new String[]{"4","Meredith Eliot","Bernard Clayton","","9/12/2014","1:35pm","65"})));
		tuples.add(new ArrayList<String>(Arrays.asList(new String[]{"5","Hale Lawson","Cindy Parks","Checkup","9/12/2014","2:15pm","73"})));
		segment = new QuerySegment(tuples);	
		
		return segment;
	}

	@Override
	public Set<String> getRelationNames() {
		Set<String> relations = new HashSet<String>();
		
		relations.add(RELATION);
		
		return relations;
	}

	@Override
	public Estimation estimate(Query query) {
		long elapsedTime = 0; // elapsed time in nano seconds
		long currentTime = 0; // current time in nano seconds
		
		// compute time:
		currentTime = System.nanoTime();
		process(query);
		elapsedTime = System.nanoTime() - currentTime;
		
		return new Estimation(elapsedTime/1000,0,0.01);
	}

    @Override
    public double getAvgTupleSize(String relation) {
        return 80;
    }

	@Override
	public int getMaxTupleSize(String relation) {
		
		int sumDataTypeSize = 0;
		
		sumDataTypeSize += 22; //NUMBER SIZE
		sumDataTypeSize += 50; //PATIENT NAME SIZE
		sumDataTypeSize += 50; //DOCTOR NAME SIZE
		sumDataTypeSize += 50; //DESCRIPTION VARCHAR2 SIZE
		sumDataTypeSize += 7; // DATE SIZE
		sumDataTypeSize += 7; //DATE SIZE
		sumDataTypeSize += 22; //NUMBER SIZE
		return sumDataTypeSize;
	}

	@Override
	public Map<String, Double> getMinValueForAttributes(String relationName) {
		
		Map<String,Double> table = new HashMap<String,Double>();
		
		table.put("NoteID",Double.valueOf(1));
		table.put("PatientName", null);
		table.put("DoctorName", null);
		table.put("Description", null);
		table.put("Date", null);
		table.put("Time", null);
		table.put("HeartRate", Double.valueOf(56));
		
		return table;
	}

	@Override
	public Map<String, Double> getMaxValueForAttributes(String relationName) {
		
		Map<String,Double> table = new HashMap<String,Double>();
		
		table.put("NoteID",Double.valueOf(5));
		table.put("PatientName", null);
		table.put("DoctorName", null);
		table.put("Description", null);
		table.put("Date", null);
		table.put("Time", null);
		table.put("HeartRate", Double.valueOf(80));
		
		return table;
	}

	@Override
	public Map<String, Long> getNbDifferentValuesForAttributes(
			String relationName) {
		
		Map<String,Long> table = new HashMap<String,Long>();
		
		table.put("NoteID",Long.valueOf(5));
		table.put("PatientName", Long.valueOf(5));
		table.put("DoctorName", Long.valueOf(5));
		table.put("Description", Long.valueOf(4));
		table.put("Date", Long.valueOf(2));
		table.put("Time", Long.valueOf(5));
		table.put("HeartRate", Long.valueOf(5));
		
		return table;
	}

}
