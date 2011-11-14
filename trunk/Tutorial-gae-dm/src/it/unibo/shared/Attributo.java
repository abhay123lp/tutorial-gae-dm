package it.unibo.shared;

import java.io.Serializable;
import java.util.Vector;

@SuppressWarnings("serial")
public class Attributo implements Serializable {
	private String name;
	private boolean isNominal;
	private boolean isNumeric;
	private Vector<String> values;
	private Double minNumericBound;
	private Double maxNumericBound;
	
	public Attributo(){}

	public Attributo(String name, boolean isNominal, boolean isNumeric,
			Vector<String> values, Double minNumericBound,
			Double maxNumericBound) {
		super();
		this.name = name;
		this.isNominal = isNominal;
		this.isNumeric = isNumeric;
		this.values = values;
		this.minNumericBound = minNumericBound;
		this.maxNumericBound = maxNumericBound;
	}


	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean isNominal() {
		return isNominal;
	}

	public void setNominal(boolean isNominal) {
		this.isNominal = isNominal;
	}

	public boolean isNumeric() {
		return isNumeric;
	}

	public void setNumeric(boolean isNumeric) {
		this.isNumeric = isNumeric;
	}

	public Vector<String> getValues() {
		return values;
	}

	public void setValues(Vector<String> values) {
		this.values = values;
	}

	public Double getMinNumericBound() {
		return minNumericBound;
	}

	public void setMinNumericBound(Double minNumericBound) {
		this.minNumericBound = minNumericBound;
	}

	public Double getMaxNumericBound() {
		return maxNumericBound;
	}

	public void setMaxNumericBound(Double maxNumericBound) {
		this.maxNumericBound = maxNumericBound;
	}
	
	public boolean isInRange(Double value) {
		if(minNumericBound<=value && maxNumericBound>=value)
			return true;
		else
			return false;
		
	}

	
	
	
}
