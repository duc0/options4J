/**
 Copyright (C) 2013 Sijuola F. Odeyemi

 This source code is released under the Artistic License 2.0.
 
 Code license: http://www.opensource.org/licenses/artistic-license-2.0.php

 Contact: odeyemis@gmail.com
 */
package com.maygard.bonds;

import com.maygard.core.NewtonYield;
import com.maygard.ir.InterestRate;

public class Volatility{
	
	public Volatility() {
		this.facevalue=1000.0;
		this.frequency=2.0;
	}
	public Volatility(double parvalue,double coupontimes) {
		this.facevalue=parvalue;
		this.frequency=coupontimes;
	}
	
	private double mktprice;
	private double mktpricelow;
	private double mktpricenew;
	private double couponvalue;//couponvalue = par value*annual
	//coupon percent/2
	private double facevalue;
	private double frequency;
	private double pv;
	private double par;
	private double relativeprice;
	private double relativepricelow;
	private double upyield;
	private double downyield;
	private double newpriceup;
	private double newpricedown;
	private double currentyield;
	private double currentpvb;
	/* Accessor methods */
	private void setInitialYldPp(double yield)
	{
	currentyield=yield;
	}
	public double getInitialPpYld()
	{
	return currentyield;
	}
	private void setPpointpriceup(double price)
	{
	newpriceup=price;
	}
	private void setPpointpricedown(double price)
	{
	newpricedown=price;
	}
	public double getPriceupPp()
	{
	return newpriceup;
	}
	public double getPricedownPp()
	{
	return newpricedown;
	}
	private void setdownyieldPp(double yield)
	{
	downyield=yield;
	}
	private void setupyieldPp(double yield)
	{
	upyield=yield;
	}
	public double getUpPp()
	{
	return upyield;
	}
	public double getDownPp()
	{
	return downyield;
	}
	public double getValuePUp()
	{
	return (Math.abs(getUpPp()-getInitialPpYld())/100.0);
	}
	public double getValuePDown()
	{
	return (Math.abs(getDownPp()-getInitialPpYld())/100.0);
	}
	private void setRelativeValue(double price)
	{
	relativeprice=price;
	}
	public double getRelativeValue()
	{
	return relativeprice;
	}
	private void setRelativeValuelow(double price)
	{
	relativepricelow=price;
	}
	public double getRelativeValuelow()
	{
	return relativepricelow;
	}
	private void setCurrentPvb(double price)
	{
	currentpvb=price;
	}
	public double getCurrentPvb()
	{
	return currentpvb;
	}
	/* Price value of a basis point */
	public void pVbPoints(double yield,double yearterm,double coupon,
	double pointchange)
	{
		double yieldval;
		mktprice=bondPrice(yield,yearterm,coupon);
		setCurrentPvb(mktprice);
		yieldval=(yield+(pointchange/100.0));// make basis point
		//adjustment higher
		mktpricenew=bondPrice(yieldval,yearterm,coupon);
		setRelativeValue(Math.abs(mktpricenew-mktprice));
		yieldval=(yield-(pointchange/100.0));// make basis point
		//adjustment lower
		mktpricelow=bondPrice(yieldval,yearterm,coupon);
		setRelativeValuelow(Math.abs(mktpricelow-mktprice));
	}
	
	/* Provides basic bond pricing */
	public double bondPrice(double yield,double yearterm,double coupon)
	{
		couponvalue=((facevalue*coupon/100)/frequency);
		pv=(couponvalue*InterestRate.presentValueAnnuityCertain((yield/100.0)/frequency,
		(frequency*yearterm)));
		par=(facevalue*(1.0/Math.pow(1.0+(yield/100.0)/frequency,
		(frequency*yearterm))));
		return(pv+par);
	}
	public double percentVolatility(double yield,double yearterm,
	double coupon,double pointchange)
	{
		pVbPoints(yield,yearterm,coupon,pointchange);//price value of a
		//basis point
		couponvalue=((facevalue*coupon/100)/frequency);
		pv=(couponvalue*InterestRate.presentValueAnnuityCertain((yield/100.0)/frequency,
		(frequency*yearterm)));
		par=(facevalue*(1.0/Math.pow(1.0+(yield/100.0)/frequency,
		(frequency*yearterm))));
		mktprice=pv+par;
		return((getRelativeValue()/mktprice)*100.0);
	}
	
	/**
	*Method provides yield values for a percentage point
	change in par value
	*Sets via accessor methods the Yield value of a point change,
	the initial yield value prior to applying the point change
	*/
	public void yieldForPpoint(double couponpercent,double price,
	double maturity,double estimate,
	double pointvalue)
	{
		double couponterm=12.0/frequency;
		double change=((facevalue/100.0)*pointvalue);
		setPpointpricedown(price-change);
		setPpointpriceup(price+change);
		
			//use newtonyield instead of interval bisection method
			NewtonYield CalcBond= new NewtonYield(estimate,1e-6,20);
			setInitialYldPp(CalcBond.yield(facevalue,couponterm,
			couponpercent,price,maturity));
			setdownyieldPp(Math.abs((CalcBond.yield(facevalue,
			couponterm,couponpercent,getPricedownPp(),
			maturity))));
			setupyieldPp(Math.abs((CalcBond.yield(facevalue,couponterm,
			couponpercent,getPriceupPp(),maturity))));				


	}
}
