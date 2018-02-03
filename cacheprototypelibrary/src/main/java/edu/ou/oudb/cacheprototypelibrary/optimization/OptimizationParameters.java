package edu.ou.oudb.cacheprototypelibrary.optimization;

import edu.ou.oudb.cacheprototypelibrary.power.HtcOneM7ulPowerReceiver;

public class OptimizationParameters {
	// Weights to be modified by the user (Default MAX / 3)
	private int time = 100 / 3;
	private int money = 100 / 3;
	private int energy = 100 / 3;

	// Getters
	public int getTime(){ return time; }
	public int getMoney(){ return money; }
	public int getEnergy(){ return energy; }

	// Setters
	public void setTime(int t){ this.time = t; }
	public void setMoney(int m){ this.money = m; }
	public void setEnergy(int e){ this.energy = e; }
	
	/** time constraint in nano seconds */
	private Long mTimeConstraint = Long.MAX_VALUE;
	
	/** money constraint in dollars */
	private Double mMoneyConstraint = Double.POSITIVE_INFINITY;
	
	/** energy constraint in milliamp-hour */
	private Double mEnergyConstraint = Double.POSITIVE_INFINITY;
	
	/**
	 * @return the timeConstraint in nano second
	 */
	public final Long getTimeConstraint() {
		return mTimeConstraint;
	}
	/**
	 * @param timeConstraint the timeConstraint to set in nano second
	 */
	public final void setTimeConstraint(Long timeConstraint) {
		this.mTimeConstraint = timeConstraint;
	}
	
	/**
	 * @return the moneyConstraint in dollars
	 */
	public final Double getMoneyConstraint() {
		return this.mMoneyConstraint;
	}
	/**
	 * @param moneyConstraint the moneyConstraint to set in dollars
	 */
	public final void setMoneyConstraint(Double moneyConstraint) {
		this.mMoneyConstraint = moneyConstraint;
	}
	
	/**
	 * @return the energyConstraint
	 */
	public Double getEnergyConstraint() {
		return this.mEnergyConstraint;
	}
	/**
	 * @param energyConstraint the energyConstraint to set
	 */
	public void setEnergyConstraint(Double energyConstraint) {
		if (energyConstraint != null)
		{
			this.mEnergyConstraint = energyConstraint;
		}
	}
	
	/**
	 * @return the energy constraint in mAh
	 */
	public final Double getBatteryLevel()
	{
		return HtcOneM7ulPowerReceiver.getInstance().getBatteryLevel();
	}
	

	
}
