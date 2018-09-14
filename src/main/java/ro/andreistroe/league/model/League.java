package ro.andreistroe.league.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;

@Entity
public class League
{
    @Id
    @GeneratedValue
    private Long id;

    private String title;
    private Integer startYear;
    private Integer endYear;
    
    @OneToMany(mappedBy = "league", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<TeamSeason> teams = new ArrayList<>();
    @OneToMany(mappedBy = "league", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Day> days = new ArrayList<>();
    
    @OneToOne(fetch=FetchType.LAZY)
    private League previousSeason;

    @OneToOne(mappedBy="previousSeason", fetch=FetchType.LAZY)
    private League nextSeason;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getTitle()
    {
        return title;
    }

    public void setTitle(String title)
    {
        this.title = title;
    }

    public Integer getStartYear()
    {
        return startYear;
    }

    public void setStartYear(Integer startYear)
    {
        this.startYear = startYear;
    }

    public Integer getEndYear()
    {
        return endYear;
    }

    public void setEndYear(Integer endYear)
    {
        this.endYear = endYear;
    }

    public List<TeamSeason> getTeams()
    {
        return teams;
    }

    public void setTeams(List<TeamSeason> teams)
    {
        this.teams = teams;
    }

    public List<Day> getDays()
    {
        return days;
    }

    public void setDays(List<Day> days)
    {
        this.days = days;
    }

    public League getPreviousSeason()
    {
        return previousSeason;
    }

    public void setPreviousSeason(League previousSeason)
    {
        this.previousSeason = previousSeason;
    }

    public League getNextSeason()
    {
        return nextSeason;
    }

    public void setNextSeason(League nextSeason)
    {
        this.nextSeason = nextSeason;
    }
}
