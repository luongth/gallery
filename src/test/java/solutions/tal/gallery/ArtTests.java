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

import org.junit.Test;

import java.time.LocalDateTime;

import static solutions.tal.gallery.ArtType.*;
import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 * @author Thuan Luong
 */
public class ArtTests {

    @Test(expected = IllegalArgumentException.class)
    public void null_artist_throws_exception() {
        new Art.Builder(PAINTING, "Name", null).build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void null_name_throws_exception() {
        new Art.Builder(PAINTING, null, "Artist").build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void blank_artist_throws_exception() {
        new Art.Builder(PAINTING, "Name", "").build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void blank_name_throws_exception() {
        new Art.Builder(PAINTING, "", "Artist").build();
    }

    @Test(expected = IllegalArgumentException.class)
    public void null_art_type_throws_exception() {
        new Art.Builder(null, "Name", "Artist");
    }

    @Test
    public void art_with_asking_price() {
        // given
        Art art = new Art.Builder(SCULPTURE, "Name", "Artist").withPrice(200.0).build();

        // when
        Double askingPrice = art.getAskingPrice().orElse(-1.0);

        // then
        assertThat(askingPrice, is(200.0));
        assertThat(art.getName(), is("Name"));
        assertThat(art.getArtist(), is("Artist"));
        assertThat(art.getArtType(), is(SCULPTURE));
        assertTrue(art.getCreatedDate().isBefore(LocalDateTime.now()));
    }

    @Test
    public void art_with_same_name_artist_and_type_are_equal() {
        // given
        Art painting1 = new Art.Builder(PAINTING, "Name", "Artist").withPrice(200.0).build();
        Art painting2 = new Art.Builder(PAINTING, "Name", "Artist").build();
        Art painting3 = new Art.Builder(PAINTING, "Name", "AnotherArtist").build();

        // then
        assertTrue(painting1.equals(painting2));
        assertFalse(painting1.equals(painting3));
        assertFalse(painting2.equals(painting3));
    }
}
