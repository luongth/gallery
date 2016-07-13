/*
 * Copyright 2016 Thuan Anh Luong
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

package solutions.tal.gallery;

import com.google.common.base.Preconditions;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.ConcurrentSkipListSet;

import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

/**
 * Simple implementation of <code>Gallery</code> that stores <code>Art</code> items in a set.
 *
 * @author Thuan Luong
 */
public final class SetBackedGalleryRepository implements Gallery {

    private final ConcurrentSkipListSet<Art> gallery = new ConcurrentSkipListSet<>();

    @Override
    public void addArt(Art art) {
        checkArtNotNull(art);
        gallery.add(art);
    }

    @Override
    public void deleteArt(Art art) {
        checkArtNotNull(art);
        gallery.remove(art);
    }

    @Override
    public Set<Art> getAllArt() {
        return Collections.unmodifiableSet(gallery);
    }

    @Override
    public List<String> getArtists() {
        return gallery.parallelStream().map(Art::getArtist).distinct().sorted().collect(toList());
    }

    @Override
    public Set<Art> getArtByArtist(String artist) {
        return gallery.parallelStream().filter(a -> a.getArtist().equals(artist)).collect(toSet());
    }

    @Override
    public Set<Art> getRecentArt() {
        LocalDateTime oneYearAgo = LocalDateTime.now().minusYears(1);
        return gallery.parallelStream().filter(a -> a.getCreatedDate().isAfter(oneYearAgo)).collect(toSet());
    }

    @Override
    public Set<Art> getArtByPrice(Double upper, Double lower) {
        return gallery.parallelStream()
                .filter(a -> isArtAskingPriceInRange(a, upper, lower))
                .collect(toSet());
    }

    private boolean isArtAskingPriceInRange(Art art, Double upper, Double lower) {
        if (art.getAskingPrice().isPresent()) {
            Double upperPrice = Optional.ofNullable(upper).orElse(art.getAskingPrice().get());
            Double lowerPrice = Optional.ofNullable(lower).orElse(art.getAskingPrice().get());
            return art.getAskingPrice().get() >= lowerPrice && art.getAskingPrice().get() <= upperPrice;
        }
        return false;
    }

    private static void checkArtNotNull(Art art) {
        Preconditions.checkArgument(Objects.nonNull(art), "The parameter 'art' cannot be null");
    }
}
