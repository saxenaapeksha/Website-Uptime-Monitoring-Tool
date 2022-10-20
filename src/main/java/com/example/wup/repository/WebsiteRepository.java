package com.example.wup.repository;

import com.example.wup.model.Website;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

@Repository
public interface WebsiteRepository extends JpaRepository<Website, Long> {

    @Query(value = "SELECT w.* from website w join check_scheduler s on s.id = w.check_id where s.id = ? ", nativeQuery = true)
    Website getDetails(int id);


    @Query(value = "SELECT w FROM Website w inner join w.checkScheduler cs WHERE \n" +
            "w.nextRun  >= :calPrev AND w.nextRun  <= :calNext \n" +
            "AND cs.active = true ")
    List<Website> getEligibleSchedules(@Param("calPrev") Date calPrev, @Param("calNext") Date calNext);
}
