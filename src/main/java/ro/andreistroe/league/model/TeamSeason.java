package ro.andreistroe.league.model;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity
public class TeamSeason
{
    @Id
    @GeneratedValue
    private Long id;
    private String wikiFCId;
    private String qId;
    private Integer yearStart;
    private Integer yearEnd;
    private Integer bergerTablePosition;

    @OneToOne(fetch = FetchType.LAZY)
    private TeamSeason previousSeason;

    @OneToOne(mappedBy = "previousSeason", fetch = FetchType.LAZY)
    private TeamSeason nextSeason;

    private int matches = 0;
    private int victories = 0;
    private int defeats = 0;
    private int goalsScored = 0;
    private int goalsAgainst = 0;

    private int initialPoints = 0;

    @ManyToOne
    private League league;

    public TeamSeason getNextSeason()
    {
        return nextSeason;
    }

    public void setNextSeason(TeamSeason nextSeason)
    {
        this.nextSeason = nextSeason;
    }

    public TeamSeason getPreviousSeason()
    {
        return previousSeason;
    }

    public void setPreviousSeason(TeamSeason previousSeason)
    {
        this.previousSeason = previousSeason;
    }

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public String getWikiFCId()
    {
        return wikiFCId;
    }

    public void setWikiFCId(String wikiFCId)
    {
        this.wikiFCId = wikiFCId;
    }

    public String getqId()
    {
        return qId;
    }

    public void setqId(String qId)
    {
        this.qId = qId;
    }

    public Integer getYearStart()
    {
        return yearStart;
    }

    public void setYearStart(Integer yearStart)
    {
        this.yearStart = yearStart;
    }

    public Integer getYearEnd()
    {
        return yearEnd;
    }

    public void setYearEnd(Integer yearEnd)
    {
        this.yearEnd = yearEnd;
    }

    public int getMatches()
    {
        return matches;
    }

    public void setMatches(int matches)
    {
        this.matches = matches;
    }

    public int getVictories()
    {
        return victories;
    }

    public void setVictories(int victories)
    {
        this.victories = victories;
    }

    public int getDefeats()
    {
        return defeats;
    }

    public void setDefeats(int defeats)
    {
        this.defeats = defeats;
    }

    public int getGoalsScored()
    {
        return goalsScored;
    }

    public void setGoalsScored(int goalsScored)
    {
        this.goalsScored = goalsScored;
    }

    public int getGoalsAgainst()
    {
        return goalsAgainst;
    }

    public void setGoalsAgainst(int goalsAgainst)
    {
        this.goalsAgainst = goalsAgainst;
    }

    public int getInitialPoints()
    {
        return initialPoints;
    }

    public void setInitialPoints(int initialPoints)
    {
        this.initialPoints = initialPoints;
    }

    public League getLeague()
    {
        return league;
    }

    public void setLeague(League league)
    {
        this.league = league;
    }

    public Integer getBergerTablePosition()
    {
        return bergerTablePosition;
    }

    public void setBergerTablePosition(Integer bergerTablePosition)
    {
        this.bergerTablePosition = bergerTablePosition;
    }
}
