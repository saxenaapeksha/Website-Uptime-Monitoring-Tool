package com.example.wup.repository;

import com.example.wup.model.CheckScheduler;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface CheckSchedulerRepository extends JpaRepository<CheckScheduler, Long> {
    @Query(value = "SELECT * FROM check_scheduler WHERE frequency = ? and frequency_unit = ?", nativeQuery = true)
    List<CheckScheduler> filterByFrequency(int number, String unit);

    @Query(value = "SELECT * FROM check_scheduler WHERE name like '%'||:name||'%' ", nativeQuery = true)
    List<CheckScheduler> filterByName(String name);

    @Transactional
    @Modifying
    @Query(value = "UPDATE check_scheduler SET active = ?1 where id = ?2 ", nativeQuery = true)
    int updateStatus(boolean status, int id);

    @Query(value = "SELECT id FROM check_scheduler WHERE websiteURL = ? and active = true order by\n" +
            "            case when frequency_unit = 'minute' then 1\n" +
            "            when frequency_unit = 'hour' then 2 end\n" +
            "            asc, frequency asc limit 1", nativeQuery = true)
    Integer existByURL(String websiteURL);
}
