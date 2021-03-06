package space.hideaway.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import org.springframework.stereotype.Repository;
import space.hideaway.model.News;

import java.util.List;

/**
 * Created by Justin Havely
 * 7/19/2017
 */
@Repository
public interface NewsRepository extends JpaRepository<News, Long>
{
    /**
     * Selects the two newest news posts.
     *
     * @return two news posts
     */
    @Query("SELECT n from News n WHERE (n.id + 2) > (SELECT MAX(n.id) from n) order by n.id desc")
    List<News> topNews();

    List<News> findAllByOrderByIdAsc();


}
