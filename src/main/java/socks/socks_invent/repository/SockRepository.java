package socks.socks_invent.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import socks.socks_invent.model.Sock;

import java.util.List;


public interface SockRepository extends JpaRepository<Sock, Long> {

    @Query("SELECT SUM(s.quantity) FROM Sock s WHERE s.color = :color AND s.cottonPercentage = :cottonpercentage")
    Integer sumByColorAndEqualCottonPercentage(@Param("color") String color,
                                          @Param("cottonpercentage") int cottonPercentage);

    @Query("SELECT SUM(s.quantity) FROM Sock s WHERE s.color = :color AND s.cottonPercentage > :cottonpercentage")
    Integer sumByColorAndMoreThanCottonPercentage(@Param("color") String color,
                                          @Param("cottonpercentage") int cottonPercentage);

    @Query("SELECT SUM(s.quantity) FROM Sock s WHERE s.color = :color AND s.cottonPercentage < :cottonpercentage")
    Integer sumByColorAndLessThanCottonPercentage(@Param("color") String color,
                                          @Param("cottonpercentage") int cottonPercentage);

    @Query("SELECT SUM(s.quantity) FROM Sock s WHERE s.color = :color AND s.cottonPercentage BETWEEN :minCottonPercentage AND :maxCottonPercentage")
    Integer sumByColorAndCottonPercentageBetween(@Param("color") String color,
                                                 @Param("mincottonpercentage") int minCottonPercentage,
                                                 @Param("maxcottonpercentage") int maxCottonPercentage);

    List<Sock> findByColorOrderByColorAsc(String color);
    List<Sock> findByCottonPercentageOrderByCottonPercentageAsc(int cottonPercentage);
    Sock findSockByColorAndCottonPercentage(String color, int cottonPercentage);
}
