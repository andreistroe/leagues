package ro.andreistroe.league.dto;

import java.util.ArrayList;
import java.util.List;

import ro.andreistroe.league.model.Day;

public class LeagueDaysMatchesDto
{
    private Long id;
    private List<Day> days = new ArrayList<>();

    public Long getId()
    {
        return id;
    }

    public void setId(Long id)
    {
        this.id = id;
    }

    public List<Day> getDays()
    {
        return days;
    }

    public void setDays(List<Day> days)
    {
        this.days = days;
    }

}
