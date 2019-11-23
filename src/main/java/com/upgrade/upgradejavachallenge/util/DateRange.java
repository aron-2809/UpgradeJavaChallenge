package com.upgrade.upgradejavachallenge.util;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;

public class DateRange implements Iterable<LocalDateTime> {

    private final LocalDateTime firstDate;
    private final LocalDateTime lastDate;

    public DateRange(LocalDateTime firstDate, LocalDateTime lastDate) {
        this.firstDate = firstDate;
        this.lastDate = lastDate;
    }

    @Override
    public Iterator<LocalDateTime> iterator() {
        return stream().iterator();
    }

    public Stream<LocalDateTime> stream() {
        return Stream.iterate(firstDate, d -> d.plusDays(1))
                .limit(ChronoUnit.DAYS.between(firstDate, lastDate) + 1);
    }

    public List<LocalDateTime> toList() {
        List<LocalDateTime> dates = new ArrayList<>();
        for (LocalDateTime d = firstDate; !d.isAfter(lastDate); d = d.plusDays(1)) {
            dates.add(d);
        }
        return dates;
    }
}