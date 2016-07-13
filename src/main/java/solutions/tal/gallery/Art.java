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
import java.util.Optional;

import static com.google.common.base.Strings.isNullOrEmpty;
import static java.util.Objects.hash;
import static java.util.Objects.nonNull;

/**
 * @author Thuan Luong
 */
public final class Art implements Comparable<Art> {

    private final ArtType artType;

    private final String name;

    private final String artist;

    private LocalDateTime createdDate;

    private Double askingPrice;

    private Art(ArtType artType, String name, String artist) {
        this.artType = artType;
        this.name = name;
        this.artist = artist;
        this.createdDate = LocalDateTime.now();
    }

    public ArtType getArtType() {
        return artType;
    }

    public String getName() {
        return name;
    }

    public String getArtist() {
        return artist;
    }

    public Optional<Double> getAskingPrice() {
        return Optional.ofNullable(askingPrice);
    }

    public LocalDateTime getCreatedDate() {
        return createdDate;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        Art art = (Art) o;

        return artType == art.artType && artist.equals(art.artist) && name.equals(art.name);

    }

    @Override
    public int hashCode() {
        return hash(artType, artist, name);
    }

    @Override
    public int compareTo(Art o) {
        int diff = artist.compareTo(o.getArtist());
        if (diff == 0) {
            diff = name.compareTo(o.getName());
            if (diff == 0) {
                diff = artType.compareTo(o.artType);
            }
        }
        return diff;
    }

    public static final class Builder {

        private final Art art;

        public Builder(ArtType artType, String name, String artist) {
            Preconditions.checkArgument(!isNullOrEmpty(name), "The parameter 'name' cannot be null");
            Preconditions.checkArgument(!isNullOrEmpty(artist), "The parameter 'artist' cannot be null");
            Preconditions.checkArgument(nonNull(artType), "The parameter 'artType' cannot be null");
            art = new Art(artType, name, artist);
        }

        public Builder createdOn(LocalDateTime createdOn) {
            Preconditions.checkArgument(nonNull(createdOn), "The parameter 'createdOn' cannot be null");
            art.createdDate = createdOn;
            return this;
        }

        public Builder withPrice(Double price) {
            Preconditions.checkArgument(nonNull(price), "The parameter 'price' cannot be null");
            art.askingPrice = price;
            return this;
        }

        public Art build() {
            return art;
        }
    }
}
