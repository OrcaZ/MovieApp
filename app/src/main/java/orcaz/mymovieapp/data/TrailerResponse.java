package orcaz.mymovieapp.data;

import java.util.List;

/**
 * POJO for retrofit movie/{id}/videos query
 */
public class TrailerResponse {
    public int id;
    public List<Trailer> results;

    public class Trailer {
        public String key;
        public String name;
        public String site;
    }
}
