package edu.ou.oudb.cacheprototypelibrary.estimationcache;

import edu.ou.oudb.cacheprototypelibrary.core.cache.Sizeable;
import edu.ou.oudb.cacheprototypelibrary.optimization.OptimizationParameters;

public class Estimation implements Sizeable{

	/** estimation duration in nano seconds */
	private Long mDuration;
	
	/** monetary cost in dollars */
	private Double mMonetaryCost;
	
	/** energy consumption in amp-hours */
	private Double mEnergy;
	
	public Estimation()
	{
		initToZero();
	}
	
	public Estimation(long duration, double energy, double monetaryCost)
	{
		this.mDuration = duration;
		this.mMonetaryCost = monetaryCost;
		this.mEnergy = energy;
	}
	
	public static Estimation add(Estimation one, Estimation other)
	{		
		long duration = one.getDuration() + other.getDuration();
		double energy = one.getEnergy() + other.getEnergy();
		double money = one.getMonetaryCost() + other.getMonetaryCost();
		
		
		return new Estimation(
				duration,
				energy,
				money
			);
	}

	public boolean isBetterThan(double scoreMobile, double scoreCloud) // this works only if it is applied to a mobile query
	{																	// can be improved
		boolean isBetterThan = false;

		if(scoreMobile<scoreCloud){
			isBetterThan = true;
		}

		return isBetterThan;
	}
	
	public boolean respectsConstraints(OptimizationParameters optParameters)
	{
		return (getDuration() <= optParameters.getTimeConstraint())
				&& (getMonetaryCost() <= optParameters.getMoneyConstraint())
				&& (getEnergy() <= optParameters.getEnergyConstraint());
	}

	
	public void add(long duration, double energy, double monetaryCost)
	{
		this.mDuration += duration;
		this.mMonetaryCost += monetaryCost;
		this.mEnergy += energy;
	}

	public void add(Estimation other)
	{
		this.mDuration += other.mDuration;
		this.mMonetaryCost += other.mMonetaryCost;
		this.mEnergy += other.mEnergy;
	}
	
	public void initToZero()
	{
		this.mDuration = Long.valueOf(0);
		this.mMonetaryCost = Double.valueOf(0);
		this.mEnergy = Double.valueOf(0);
	}
	
	public void initToInfinity()
	{
		this.mDuration = Long.MAX_VALUE;
		this.mMonetaryCost = Double.POSITIVE_INFINITY;
		this.mEnergy = Double.POSITIVE_INFINITY;
	}
	
	public void init(Estimation other)
	{
		this.mDuration = other.getDuration();
		this.mMonetaryCost = other.getMonetaryCost();
		this.mEnergy = other.getEnergy();
	}
	
	public void init(long duration, double energy, double monetaryCost)
	{
		this.mDuration = duration;
		this.mMonetaryCost = monetaryCost;
		this.mEnergy = energy;
	}
	
	/**
	 * @return the duration
	 */
	public final long getDuration() {
		return this.mDuration;
	}

	/**
	 * @param duration the duration to set
	 */
	public final void setDuration(long duration) {
		if (duration >= 0)
		{
			this.mDuration = duration;
		}
	}
	

	/**
	 * @return the monetaryCost
	 */
	public final double getMonetaryCost() {
		return this.mMonetaryCost;
	}

	/**
	 * @param monetaryCost the monetaryCost to set
	 */
	public final void setMonetaryCost(double monetaryCost) {
		if (monetaryCost >= 0)
		{
			this.mMonetaryCost = monetaryCost;
		}
	}
	

	/**
	 * @return the energy
	 */
	public final double getEnergy() {
		return this.mEnergy;
	}

	/**
	 * @param energy the energy to set
	 */
	public final void setEnergy(double energy) {
		if (energy >= 0)
		{
			this.mEnergy = energy;
		}
	}

	@Override
	public long size() {
		return Long.SIZE + Double.SIZE + Double.SIZE;
	}
	
	
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("time: ");
		sb.append(getDuration());
		sb.append(" | energy: ");
		sb.append(getEnergy());
		sb.append(" | money: ");
		sb.append(getMonetaryCost());
		return sb.toString();
	}
	
}
