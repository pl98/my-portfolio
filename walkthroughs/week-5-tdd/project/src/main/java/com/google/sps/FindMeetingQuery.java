// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.google.sps;

import java.util.*;
import java.util.stream.Collectors;

public final class FindMeetingQuery {
    public Collection<TimeRange> query(Collection<Event> events, MeetingRequest request) {
        Collection<String> attendees = request.getAttendees();
        Collection<String> optional = request.getOptionalAttendees();
        long duration = request.getDuration();
        List<TimeRange> occupied = new ArrayList<>();

        // iterate over existing events, keeping track of events with attendees that are included in the request 
        for (Event event : events) {
            if (hasOverlap(event, attendees)) {
                occupied.add(event.getWhen());
            }
        }  

        // merge overlapping events and then invert to find available times
        List<TimeRange> available = invert(combine(occupied));
        
        // filter for available times that are long enough 
        available = available.stream().filter(event -> event.duration() >= duration).collect(Collectors.toList()); 
        
        // if there are no available times for the mandatory attendees, or there are no optional attendees, there's no need to check optional attendees
        if (available.isEmpty() || available == null || optional.isEmpty() || optional == null) {
            return available;
        }

        List<TimeRange> availableOptional = new ArrayList<>();
        List<TimeRange> personEvents = new ArrayList<>();

        for (String person : optional) {
            personEvents.clear();
            personEvents = occupied;

            for (Event event : events) {
                if (event.getAttendees().contains(person)) {
                    personEvents.add(event.getWhen());
                }
            }

            personEvents = invert(combine(personEvents));
            personEvents = personEvents.stream().filter(event -> event.duration() >= duration).collect(Collectors.toList());

            if (!personEvents.isEmpty() && personEvents != null) {
                availableOptional.addAll(personEvents);
            }
        }

        availableOptional = combine(availableOptional);
        
        // if no times available, return original result
        if (availableOptional.isEmpty() || availableOptional == null) {
            if (!attendees.isEmpty()) {
                return available;
            }
            else {
                return availableOptional;
            }
        }

        return availableOptional;
    }

    private boolean hasOverlap(Event event, Collection<String> attendees) {
        Set<String> eventAttendees = new HashSet<>(event.getAttendees());

        eventAttendees.retainAll(attendees);

        return !eventAttendees.isEmpty();
    } 

    private List<TimeRange> combine(List<TimeRange> times) {
        if (times.isEmpty() || times == null) {
            return new ArrayList<>();
        }

        times.sort(TimeRange.ORDER_BY_START);
        List<TimeRange> result = new ArrayList<>();
        TimeRange prev = times.get(0);

        for (TimeRange time : times) {
            if (!prev.contains(time)) {
                if(prev.overlaps(time)) {
                    prev = TimeRange.fromStartEnd(prev.start(), time.end(), false);
                }
                else {
                    result.add(prev);
                    prev = time;
                }
            }
        }

        result.add(prev);
        return result;
    }

    private List<TimeRange> invert(List<TimeRange> times) {
        int start = TimeRange.START_OF_DAY;
        List<TimeRange> result = new ArrayList<>();

        if (!times.isEmpty() && times.get(0).start() == TimeRange.START_OF_DAY && times.get(0).end() == TimeRange.END_OF_DAY) {
            return new ArrayList<>();
        }

        for (TimeRange time : times) {
            int end = time.start();
            result.add(TimeRange.fromStartEnd(start, end, false));
            start = time.end();
        }

        result.add(TimeRange.fromStartEnd(start, TimeRange.END_OF_DAY, true));
        return result;
    }
}
