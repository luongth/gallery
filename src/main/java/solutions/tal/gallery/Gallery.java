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

import java.util.List;
import java.util.Set;

/**
 * Strategy to provide inventory management for art pieces in a gallery
 *
 * @author Thuan Luong
 */
public interface Gallery {

    /**
     * Add the specified art piece to the gallery, replacing an equivalent in the gallery if existing.
     *
     * @param art the art piece to add to the gallery
     */
    void addArt(Art art);

    /**
     * Remove the specified art piece from the gallery if found.
     *
     * @param art he art piece to remove from the gallery
     */
    void deleteArt(Art art);

    /**
     * Return the collection of art pieces in the gallery, is cannot be modified.
     *
     * @return all the art pieces in the gallery
     */
    Set<Art> getAllArt();

    /**
     * Return list of unique artists with art represented in the gallery, in alphabetical order.
     *
     * @return list of artists
     */
    List<String> getArtists();

    /**
     * Return all art for the specified artist
     *
     * @param artist the artist for which to retrieve all art pieces
     *
     * @return all the art pieces for the artist
     */
    Set<Art> getArtByArtist(String artist);

    /**
     * Return all art pieces created in the gallery within the past year
     *
     * @return all art pieces created in the gallery within the past year
     */
    Set<Art> getRecentArt();

    /**
     * Return all art pieces with an asking price in between the specified upper and lower bands.
     *
     * @param upper the upper price band
     * @param lower the lower price band
     *
     * @return all art pieces with an asking price in between the specified upper and lower bands.
     */
    Set<Art> getArtByPrice(Double upper, Double lower);
}
