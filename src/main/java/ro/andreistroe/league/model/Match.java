package ro.andreistroe.league.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

@Entity
public class Match
{
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    private TeamSeason hosts;
    @ManyToOne
    private TeamSeason guests;
    private int hostsGoals;
    private int guestsGoals;
    @ManyToOne
    private Day day;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public TeamSeason getHosts()
    {
        return hosts;
    }

    public void setHosts(TeamSeason hosts)
    {
        this.hosts = hosts;
    }

    public TeamSeason getGuests()
    {
        return guests;
    }

    public void setGuests(TeamSeason guests)
    {
        this.guests = guests;
    }

    public int getHostsGoals()
    {
        return hostsGoals;
    }

    public void setHostsGoals(int hostsGoals)
    {
        this.hostsGoals = hostsGoals;
    }

    public int getGuestsGoals()
    {
        return guestsGoals;
    }

    public void setGuestsGoals(int guestsGoals)
    {
        this.guestsGoals = guestsGoals;
    }

    public Day getDay()
    {
        return day;
    }

    public void setDay(Day day)
    {
        this.day = day;
    }
}
