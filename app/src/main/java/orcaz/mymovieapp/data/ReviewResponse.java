package orcaz.mymovieapp.data;

import java.util.List;

/**
 * POJO for retrofit movie/{id}/reviews query
 */
public class ReviewResponse {
    public int id;
    public List<Review> results;

    public class Review {
        public String author;
        public String content;
    }

    public void AddReview(String author, String content){
        Review r = new Review();
        r.author = author;
        r.content = content;
        results.add(r);
    }
}
