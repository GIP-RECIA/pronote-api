/*
 * Copyright © 2026 GIP-RECIA (https://www.recia.fr/)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package fr.recia.pronote.pronoteapi.ical.impl;

import fr.recia.pronote.pronoteapi.ical.IcsCalendarParser;
import fr.recia.pronote.pronoteapi.ical.IcsEvent;
import net.fortuna.ical4j.data.CalendarBuilder;
import net.fortuna.ical4j.data.ParserException;
import net.fortuna.ical4j.model.Calendar;
import net.fortuna.ical4j.model.Component;
import net.fortuna.ical4j.model.Property;
import net.fortuna.ical4j.model.TextList;
import net.fortuna.ical4j.model.component.VEvent;
import net.fortuna.ical4j.model.property.*;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class Ical4JCalendarParser implements IcsCalendarParser {
    @Override
    public List<IcsEvent> parse(InputStream icsStream) throws ParserException, IOException {
        Calendar calendar = new CalendarBuilder().build(icsStream);
        List<VEvent> vEvents = calendar.<VEvent>getComponents(Component.VEVENT);
        List<IcsEvent> events = new ArrayList<>();
        for (VEvent vEvent : vEvents){
            String uid = vEvent.<Uid>getProperty(Property.UID).map(Uid::getValue).orElse(null);
            String summary = vEvent.<Summary>getProperty(Property.SUMMARY).map(Summary::getValue).orElse(null);
            String description = vEvent.<Description>getProperty(Property.DESCRIPTION).map(Description::getValue).orElse(null);
            String location = vEvent.<Location>getProperty(Property.LOCATION).map(Location::getValue).orElse(null);
            Temporal start = Optional.ofNullable(vEvent.<Temporal>getDateTimeStart())
                    .map(DtStart::getDate)
                    .orElse(null);

            Temporal end = vEvent.getEndDate().map(DtEnd::getDate).orElse(null);
            List<String> categories = vEvent.<Categories>getProperty(Property.CATEGORIES)
                    .map(Categories::getCategories)   // Optional<TextList>
                    .map(TextList::getTexts)          // Optional<Set<String>>
                    .map(List::copyOf)                // Optional<List<String>>
                    .orElse(List.of());
            events.add(
                    new IcsEvent(uid,start,end,summary,description,location,categories)
            );
        }
        return events;
    }
}
