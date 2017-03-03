package space.hideaway.util;

import space.hideaway.model.Data;
import space.hideaway.model.StationStatistics;

import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by dough on 2017-02-19.
 */
public class SortingUtils
{
    public static List<Data> sortMostRecentFirst(Set<Data> dataList)
    {
        return dataList.stream()
                       .sorted(Comparator.comparing(Data::getDateTime).reversed())
                       .collect(Collectors.toList());
    }

    public static List<StationStatistics> sortMostRecentFirst(List<StationStatistics> stationStatisticsList)
    {
        return stationStatisticsList.stream()
                                    .sorted(Comparator.comparing(StationStatistics::getDate).reversed()).collect
                        (Collectors.toList());
    }
}