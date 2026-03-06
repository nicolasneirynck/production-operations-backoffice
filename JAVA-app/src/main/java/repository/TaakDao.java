package repository;

import domein.Taak;

import java.util.List;

public interface TaakDao extends GenericDao<Taak>{
    public List<String> findAllTaakTypes();
}
