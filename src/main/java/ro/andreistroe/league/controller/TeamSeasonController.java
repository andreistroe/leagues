package ro.andreistroe.league.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.view.RedirectView;

import ro.andreistroe.league.model.League;
import ro.andreistroe.league.model.TeamSeason;
import ro.andreistroe.league.repository.LeagueRepository;
import ro.andreistroe.league.repository.TeamSeasonRepository;

@Controller
public class TeamSeasonController
{
    @Autowired
    private TeamSeasonRepository repo;

    @Autowired
    private LeagueRepository leagueRepo;

    @RequestMapping(value = { "/ui/addTeamToLeague/{leagueId}", "/ui/editTeam/{id}" })
    public String editTeam(Model model, @PathVariable(required = false, name = "id") Long id, @PathVariable(required = false, name = "leagueId") Long leagueId)
    {
        TeamSeason editable = null;
        if (null != leagueId)
        {
            editable = new TeamSeason();
            editable.setLeague(leagueRepo.findById(leagueId).get());
        }
        else
        {
            editable = repo.findById(id).get();
        }
        model.addAttribute("team", editable);
        return "editTeam";
    }

    @RequestMapping(value = { "/ui/saveTeam" }, method = RequestMethod.POST)
    public RedirectView saveTeam(Model model, TeamSeason team)
    {
        team.setYearStart(team.getLeague().getStartYear());
        team.setYearEnd(team.getLeague().getEndYear());
        repo.save(team);
        model.addAttribute("team", repo.findAll());
        return new RedirectView("/ui/editLeague/" + team.getLeague().getId());
    }

}
