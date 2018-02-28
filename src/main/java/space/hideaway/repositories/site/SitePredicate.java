package space.hideaway.repositories.site;


import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.core.types.dsl.NumberPath;
import com.querydsl.core.types.dsl.PathBuilder;
import com.querydsl.core.types.dsl.StringPath;
import org.apache.commons.lang3.math.NumberUtils;
import org.json.JSONArray;
import space.hideaway.model.site.Site;
import space.hideaway.model.site.SiteMetadata;

import java.util.ArrayList;

public class SitePredicate {

    private SearchCriteria criteria;

    public SitePredicate(SearchCriteria criteria) {
        this.criteria = criteria;
    }

    public BooleanExpression getPredicate() {
        PathBuilder<SiteMetadata> entityPath = new PathBuilder<SiteMetadata>(SiteMetadata.class, "siteMetadata");

        if (NumberUtils.isNumber(criteria.getValue().toString())) {
            NumberPath<Double> path = entityPath.getNumber(criteria.getKey(), Double.class);
            double value = Double.parseDouble(criteria.getValue().toString());
            switch (criteria.getOperation()) {
                case "equal":
                    return path.eq(value);
                case "greater":
                    return path.goe(value);
                case "less":
                    return path.loe(value);
                case "not_equal":
                    return path.ne(value);
            }
        }
        else if (criteria.getValue() instanceof JSONArray) {
            JSONArray list = (JSONArray) criteria.getValue();
            if (list.length() >= 2 && NumberUtils.isNumber(list.get(0).toString()) && NumberUtils.isNumber(list.get(1).toString())) {
                NumberPath<Double> path = entityPath.getNumber(criteria.getKey(), Double.class);
                double value1 = Double.parseDouble(list.get(0).toString());
                double value2 = Double.parseDouble(list.get(1).toString());
                switch (criteria.getOperation()) {
                    case "between":
                        return path.between(value2, value1);
                }
            }
        }
        else {
            StringPath path = entityPath.getString(criteria.getKey());
            if (criteria.getOperation().equalsIgnoreCase("equal")) {
                return path.equalsIgnoreCase(criteria.getValue().toString());
            } else if (criteria.getOperation().equalsIgnoreCase("not_equal")) {
                return path.notEqualsIgnoreCase(criteria.getValue().toString());
            }
        }
        return null;
    }
}
