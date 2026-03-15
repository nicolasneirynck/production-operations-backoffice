package domein.controllers;

import dto.TeamDTO;
import exception.ValidationException;

import java.util.List;
import java.util.Optional;

public interface MijnTeamService {
    Optional<TeamDTO> getMijnTeam();
    void updateMijnTeam(String teamCode, List<Long> werknemerIds) throws ValidationException;
}
