package com.simplevat.dao;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;

public interface Dao<PK, ENTITY> {

	public ENTITY findByPK(PK pk);

	public List<ENTITY> executeNamedQuery(String namedQuery);

	public ENTITY persist(ENTITY entity);

	public ENTITY update(ENTITY entity);

	public void delete(ENTITY entity);

	public List<ENTITY> findByAttributes(Map<String, String> attributes);

	public List<ENTITY> filter(AbstractFilter<ENTITY> filter);

	public EntityManager getEntityManager();

	public void importData(List<ENTITY> entities);

	/**
	 * This method should just get ALL the data, which is different than findALL
	 * and hence given so unique name
	 * 
	 * @return
	 */
	public List<ENTITY> dumpData();
}
