package ro.andreistroe.league.controller;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import ro.andreistroe.league.model.Day;
import ro.andreistroe.league.model.League;
import ro.andreistroe.league.model.Match;
import ro.andreistroe.league.model.TeamSeason;
import ro.andreistroe.league.repository.LeagueRepository;

@Controller
public class LeagueController
{

    @Autowired
    LeagueRepository repo;

    @RequestMapping(value = "/")
    public String leagueList(Model model)
    {
        model.addAttribute("leagues", repo.findAll());
        return "leagueList";
    }

    @RequestMapping(value = { "/ui/editLeague", "/ui/editLeague/{id}" })
    public String editLeague(Model model, @PathVariable(required = false, name = "id") Long id)
    {
        model.addAttribute("league", null == id ? new League() : repo.findById(id).get());
        return "editLeague";
    }

    @RequestMapping(value = { "/ui/saveLeague" }, method = RequestMethod.POST)
    public RedirectView saveLeague(Model model, League league)
    {
        repo.save(league);
        model.addAttribute("leagues", repo.findAll());
        return new RedirectView("/");
    }

    @RequestMapping(value = { "/ui/deleteLeague/{id}" })
    public RedirectView deleteLeague(Model model, @PathVariable(required = true, name = "id") Long id)
    {
        Optional<League> leagueOpt = repo.findById(id);
        if (leagueOpt.isPresent())
        {
            League league = leagueOpt.get();
            if (league.getTeams().isEmpty() && league.getDays().isEmpty())
            {
                repo.delete(league);
            }
        }
        return new RedirectView("/");
    }

    @RequestMapping(value = { "/ui/generateMatches/{leagueId}" })
    public ModelAndView generateMatches(Model model, @PathVariable(required = true, name = "leagueId") Long leagueId)
    {
        //check preconditions & build berger table
        League league = repo.findById(leagueId).get();

        List<TeamSeason> teams = league.getTeams();
        int bergerTableSize = teams.size();
        if (2 > bergerTableSize)
        {
            model.addAttribute("error", "There are not enough teams to create a league. Please add more teams.");
            ModelAndView mav = new ModelAndView("generationError");
            mav.addAllObjects(model.asMap());
            return mav;
        }
        if (0 < league.getDays().size())
        {
            model.addAttribute("error", "Days already generated. Delete all days in the league and try again.");
            ModelAndView mav = new ModelAndView("generationError");
            mav.addAllObjects(model.asMap());
            return mav;
        }

        if (1 == bergerTableSize % 2)
        {
            bergerTableSize++;
        }
        TeamSeason[] bergerTable = new TeamSeason[bergerTableSize];
        for (TeamSeason eachTeam : teams)
        {
            Integer bergerPosn = eachTeam.getBergerTablePosition();
            if (null == bergerPosn)
            {
                model.addAttribute("error", String.format("Team %s has no berger table position. Delete the team or assign it a berger table position.", eachTeam.getWikiFCId()));
                ModelAndView mav = new ModelAndView("generationError");
                mav.addAllObjects(model.asMap());
                return mav;
            }
            if (null != bergerTable[bergerPosn.intValue() - 1])
            {
                model.addAttribute("error", String.format("Multiple teams for berger table position %d. Please reassign berger table positions.", bergerPosn));
                ModelAndView mav = new ModelAndView("generationError");
                mav.addAllObjects(model.asMap());
                return mav;
            }
            bergerTable[bergerPosn.intValue() - 1] = eachTeam;
        }

        //prepare round-robin generator
        int centerPoint = bergerTableSize - 1;
        int header = 0;
        int[] leftSide = new int[bergerTableSize / 2 - 1];
        for (int i = 0; i < leftSide.length; i++)
        {
            leftSide[i] = i + 1;
        }
        int[] rightSide = new int[bergerTableSize / 2 - 1];
        for (int i = rightSide.length - 1; i >= 0; i--)
        {
            rightSide[i] = bergerTableSize - i - 2;
        }
        int[] dayOrder = new int[bergerTableSize - 1];
        dayOrder[0] = 0;
        /*
        int crtIdx = dayOrder.length - 2;
        for (int dayIdx = 1; dayIdx < dayOrder.length; dayIdx++)
        {
            dayOrder[dayIdx] = crtIdx;
            crtIdx -= 2;
            if (crtIdx < 0)
            {
                crtIdx = dayOrder.length - 1;
            }
        }
        */

        for (int i = 0; i < dayOrder.length; i++)
        {
            if (i % 2 != 0)
            {
                dayOrder[(i + dayOrder.length) / 2] = i;
            }
            else
            {
                dayOrder[i / 2] = i;
            }
        }

        //for (int i = 1; i < dayOrder.length; i++) dayOrder[i] = i;
        //round-robin generator loop
        for (int dayIdx = 0; dayIdx < bergerTableSize - 1; dayIdx++)
        {
            Day crtDay = new Day();
            crtDay.setLeague(league);
            crtDay.setIndex(1 + dayOrder[dayIdx]);

            Match topMatch = new Match();
            topMatch.setDay(crtDay);
            topMatch.setHosts(0 == dayOrder[dayIdx] % 2 ? bergerTable[header] : bergerTable[centerPoint]);
            topMatch.setGuests(0 == dayOrder[dayIdx] % 2 ? bergerTable[centerPoint] : bergerTable[header]);
            crtDay.getMatches().add(topMatch);

            for (int i = 0; i < leftSide.length; i++)
            {
                Match eachMatch = new Match();
                eachMatch.setDay(crtDay);
                eachMatch.setHosts(0 == dayIdx % 2 ? bergerTable[leftSide[i]] : bergerTable[rightSide[i]]);
                eachMatch.setGuests(0 == dayIdx % 2 ? bergerTable[rightSide[i]] : bergerTable[leftSide[i]]);
                crtDay.getMatches().add(eachMatch);
            }
            league.getDays().add(crtDay);

            int aux = header;
            header = leftSide[0];
            for (int i = 0; i < leftSide.length - 1; i++)
            {
                leftSide[i] = leftSide[i + 1];
            }
            leftSide[leftSide.length - 1] = rightSide[rightSide.length - 1];
            for (int i = rightSide.length - 1; i > 0; i--)
            {
                rightSide[i] = rightSide[i - 1];
            }
            rightSide[0] = aux;
        }
        //generate second season part

        List<Day> secondPartDays = new ArrayList<>();
        for (Day eachDay : league.getDays())
        {
            Day newDay = new Day();
            newDay.setLeague(league);
            newDay.setIndex(eachDay.getIndex() + bergerTableSize - 1);

            for (Match eachMatch : eachDay.getMatches())
            {
                Match newMatch = new Match();
                newMatch.setDay(newDay);
                newMatch.setGuests(eachMatch.getHosts());
                newMatch.setHosts(eachMatch.getGuests());
                newDay.getMatches().add(newMatch);
            }
            secondPartDays.add(newDay);
        }
        league.getDays().addAll(secondPartDays);
        repo.save(league);

        return new ModelAndView(String.format("redirect:/ui/viewMatches/%d", leagueId));
    }

    @RequestMapping(value = "/ui/getRankings/{id}")
    public String listRankings(Model model, @PathVariable(value = "id", required = true) Long leagueId)
    {
        League league = repo.findById(leagueId).get();
        computeRankings(league);

        model.addAttribute("league", league);
        return "rankings";
    }

    private void computeRankings(League league)
    {
        league.getTeams().stream().forEach(t -> {
            t.setPlayed(0);
            t.setDefeats(0);
            t.setVictories(0);
            t.setDraws(0);
            t.setGoalsAgainst(0);
            t.setGoalsScored(0);
        });

        league.getDays().stream().flatMap(d -> d.getMatches().stream()).filter(m -> null != m.getHostsGoals() && null != m.getGuestsGoals()).forEach(m -> {
            TeamSeason hosts = m.getHosts();
            TeamSeason guests = m.getGuests();
            hosts.setGoalsScored(hosts.getGoalsScored() + m.getHostsGoals());
            hosts.setGoalsAgainst(hosts.getGoalsAgainst() + m.getGuestsGoals());
            guests.setGoalsScored(guests.getGoalsScored() + m.getGuestsGoals());
            guests.setGoalsAgainst(guests.getGoalsAgainst() + m.getHostsGoals());
            hosts.setPlayed(1 + hosts.getPlayed());
            guests.setPlayed(1 + guests.getPlayed());
            hosts.setVictories(hosts.getVictories() + (m.getHostsGoals() > m.getGuestsGoals() ? 1 : 0));
            hosts.setDraws(hosts.getDraws() + (m.getHostsGoals() == m.getGuestsGoals() ? 1 : 0));
            hosts.setDefeats(hosts.getDefeats() + (m.getHostsGoals() < m.getGuestsGoals() ? 1 : 0));
            guests.setVictories(guests.getVictories() + (m.getHostsGoals() < m.getGuestsGoals() ? 1 : 0));
            guests.setDraws(guests.getDraws() + (m.getHostsGoals() == m.getGuestsGoals() ? 1 : 0));
            guests.setDefeats(guests.getDefeats() + (m.getHostsGoals() > m.getGuestsGoals() ? 1 : 0));
        });

        league.getTeams().sort(new Comparator<TeamSeason>()
        {

            @Override
            public int compare(TeamSeason o1, TeamSeason o2)
            {
                int points1 = 3 * o1.getVictories() + o1.getDraws() + o1.getInitialPoints();
                int points2 = 3 * o2.getVictories() + o2.getDraws() + o2.getInitialPoints();
                if (points1 != points2)
                {
                    return points2 - points1;
                }
                int goaldiff1 = o1.getGoalsScored() - o1.getGoalsAgainst();
                int goaldiff2 = o2.getGoalsScored() - o2.getGoalsAgainst();
                if (goaldiff1 != goaldiff2)
                {
                    return goaldiff2 - goaldiff1;
                }

                return o2.getGoalsScored() - o1.getGoalsScored();
            }
        });
    }

    @RequestMapping(value = { "/ui/exportToWiki/{id}" })
    public String exportToWiki(Model model, @PathVariable(value = "id", required = true) Long id)
    {
        League l = repo.findById(id).get();
        StringBuilder resultsTemplate = new StringBuilder();
        List<TeamSeason> teams = l.getTeams();
        teams.sort(new Comparator<TeamSeason>()
        {

            @Override
            public int compare(TeamSeason o1, TeamSeason o2)
            {
                return o1.getBergerTablePosition() - o2.getBergerTablePosition();
            }
        });
        for (TeamSeason eachTeam : teams)
        {
            resultsTemplate.append("| team").append(eachTeam.getBergerTablePosition()).append(" = ").append(eachTeam.getShortName());
        }

        resultsTemplate.append("\n");
        for (TeamSeason eachTeam : teams)
        {
            resultsTemplate.append("\n| name_").append(eachTeam.getShortName()).append(" = ").append("{{ClubFotbal|").append(eachTeam.getWikiFCId()).append('|')
                .append(l.getEndYear()).append("-06").append("}}");
        }
        Calendar cal = Calendar.getInstance();
        StringBuilder dateBuilder = new StringBuilder("{{Dată|"); 
        dateBuilder.append(cal.get(Calendar.YEAR)).append('|').append(1 + cal.get(Calendar.MONTH)).append('|').append(cal.get(Calendar.DAY_OF_MONTH))
            .append("}}\n");
        resultsTemplate.append("\n\n| update = ").append(dateBuilder.toString()).append("\n");

        List<Day> days = l.getDays();
        days.sort(new Comparator<Day>()
        {
            @Override
            public int compare(Day o1, Day o2)
            {
                return o1.getIndex() - o2.getIndex();
            }
        });
        for (Day eachDay : days)
        {
            boolean dayPlayed = false;
            for (Match eachMatch : eachDay.getMatches())
            {
                if (null != eachMatch.getGuestsGoals() && null != eachMatch.getHostsGoals())
                {
                    resultsTemplate.append("\n| match_").append(eachMatch.getHosts().getShortName()).append('_').append(eachMatch.getGuests().getShortName()).append(" = ")
                        .append(eachMatch.getHostsGoals()).append("–").append(eachMatch.getGuestsGoals());
                    dayPlayed = true;
                }
            }
            if (dayPlayed)
            {
                resultsTemplate.append("\n");
            }
        }
        model.addAttribute("results", resultsTemplate.toString());

        StringBuilder rankingsTemplate = new StringBuilder();
        rankingsTemplate.append("\n\n| update = ").append(dateBuilder.toString()).append("\n");

        computeRankings(l);
        int rank = 0;
        for (TeamSeason eachTeam : l.getTeams())
        {
            rank++;
            rankingsTemplate.append("|team").append(rank).append('=').append(eachTeam.getShortName());
        }
        rankingsTemplate.append("\n");
        for (TeamSeason eachTeam : l.getTeams())
        {
            rankingsTemplate.append("\n");
            rankingsTemplate.append("|win_").append(eachTeam.getShortName()).append('=').append(eachTeam.getVictories());
            rankingsTemplate.append("|draw_").append(eachTeam.getShortName()).append('=').append(eachTeam.getDraws());
            rankingsTemplate.append("|loss_").append(eachTeam.getShortName()).append('=').append(eachTeam.getDefeats());
            rankingsTemplate.append("|gf_").append(eachTeam.getShortName()).append('=').append(eachTeam.getGoalsScored());
            rankingsTemplate.append("|ga_").append(eachTeam.getShortName()).append('=').append(eachTeam.getGoalsAgainst());
            rankingsTemplate.append("<!-- ").append(eachTeam.getWikiFCId()).append(" -->");
        }

        rankingsTemplate.append("\n");
        for (TeamSeason eachTeam : teams)
        {
            if (0 != eachTeam.getInitialPoints())
            {
                rankingsTemplate.append("| adjust_points_").append(eachTeam.getShortName()).append(" = ").append(eachTeam.getInitialPoints());
            }
        }

        rankingsTemplate.append("\n\n");

        for (TeamSeason eachTeam : teams)
        {
            rankingsTemplate.append("| name_").append(eachTeam.getShortName()).append(" = ").append("{{ClubFotbal|").append(eachTeam.getWikiFCId()).append('|')
                .append(l.getEndYear()).append("-06").append("}}");
        }

        model.addAttribute("rankings", rankingsTemplate.toString());
        return "exported";
    }
}
