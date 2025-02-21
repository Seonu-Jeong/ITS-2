package org.sparta.its.domain.hall.repository;

import java.sql.PreparedStatement;
import java.util.List;

import javax.sql.DataSource;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * create on 2025. 01. 09.
 * create by IntelliJ IDEA.
 *
 * 공연장 관련 JDBCRepository.
 *
 * @author TaeHyeon Kim
 */
@Repository
public class HallBulkRepository {

	private final JdbcTemplate jdbcTemplate;

	public HallBulkRepository(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public void saveAllHallImage(Long hallId, List<String> urls) {
		String sql = "INSERT INTO hall_image (hall_id, image_url) " +
			"VALUES (?,?)";

		jdbcTemplate.batchUpdate(sql,
			urls,
			urls.size(),
			(PreparedStatement ps, String url) -> {
				ps.setLong(1, hallId);
				ps.setString(2, url);
			});
	}

	public void saveAllSeat(Long hallId, List<Integer> seatNumbers) {
		String sql = "INSERT INTO seat (hall_id, seat_number) " +
			"VALUES (?,?)";

		jdbcTemplate.batchUpdate(sql,
			seatNumbers,
			seatNumbers.size(),
			(PreparedStatement ps, Integer seatNumber) -> {
				ps.setLong(1, hallId);
				ps.setInt(2, seatNumber);
			});
	}

}
