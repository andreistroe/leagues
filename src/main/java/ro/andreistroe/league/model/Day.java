package ro.andreistroe.league.model;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

@Entity
public class Day
{
    @Id
    @GeneratedValue
    private Long id;

    private int index;
    @OneToMany(mappedBy = "day", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Match> matches;
    private Date startDate;
    private Date endDate;
    @ManyToOne
    private League league;

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public int getIndex()
    {
        return index;
    }

    public void setIndex(int index)
    {
        this.index = index;
    }

    public List<Match> getMatches()
    {
        return matches;
    }

    public void setMatches(List<Match> matches)
    {
        this.matches = matches;
    }

    public Date getStartDate()
    {
        return startDate;
    }

    public void setStartDate(Date startDate)
    {
        this.startDate = startDate;
    }

    public Date getEndDate()
    {
        return endDate;
    }

    public void setEndDate(Date endDate)
    {
        this.endDate = endDate;
    }

    public League getLeague()
    {
        return league;
    }

    public void setLeague(League league)
    {
        this.league = league;
    }
}
