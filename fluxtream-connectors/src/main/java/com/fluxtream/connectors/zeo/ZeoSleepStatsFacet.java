package com.fluxtream.connectors.zeo;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import com.fluxtream.connectors.annotations.ObjectTypeSpec;
import org.hibernate.search.annotations.Indexed;

import com.fluxtream.domain.AbstractFloatingTimeZoneFacet;

@Entity(name="Facet_ZeoSleepStats")
@NamedQueries({
		@NamedQuery(name = "zeo.sleep.deleteAll", query = "DELETE FROM Facet_ZeoSleepStats facet WHERE facet.guestId=?"),
		@NamedQuery(name = "zeo.sleep.between", query = "SELECT facet FROM Facet_ZeoSleepStats facet WHERE facet.guestId=? AND facet.start>=(?-3600000L*10) AND facet.end<=?")
})
@ObjectTypeSpec(name = "sleep", value = 1, parallel=true, prettyname = "Sleep")
@Indexed
public class ZeoSleepStatsFacet extends AbstractFloatingTimeZoneFacet {

	public int zq;
	public Date bedTime;
	public Date riseTime;
	public int awakenings;
	public int morningFeel;
	public int totalZ;
	public int timeInDeepPercentage;
	public int timeInLightPercentage;
	public int timeInRemPercentage;
	public int timeInWakePercentage;
	public int timeToZ;
	
	@Lob
	public String sleepGraph;
	
	@Override
	protected void makeFullTextIndexable() {}
	
}