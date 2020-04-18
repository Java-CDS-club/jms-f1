package sender;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Bolid implements Serializable {

	private static final long serialVersionUID = 1L;
	double engineTemperature;
	double tiresPressure;
	double oilPressure;
	Date date;
	SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");

	public Bolid(){}
	
	public Bolid(float engineTemperature, float tirePressure, float oilPressure, Date date){
		this.engineTemperature = engineTemperature;
		this.tiresPressure = tirePressure;
		this.oilPressure = oilPressure;
		this.date = date;
	}

	public double getEngineTemperature() {
		return engineTemperature;
	}

	public void setEngineTemperature(double engineTemperature) {
		this.engineTemperature = engineTemperature;
	}

	public double getTiresPressure() {
		return tiresPressure;
	}

	public void setTiresPressure(double tirePressure) {
		this.tiresPressure = tirePressure;
	}

	public double getOilPressure() {
		return oilPressure;
	}

	public void setOilPressure(double oilPressure) {
		this.oilPressure = oilPressure;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}
	
	@Override
	public String toString(){
		String state = date + ": engineTemperature: " + engineTemperature + ", tirePressure: " + tiresPressure + " oilPressure: " + oilPressure;
		return state;
	}
	
}
