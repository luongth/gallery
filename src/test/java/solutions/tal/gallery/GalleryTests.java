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
import java.util.List;
import java.util.Set;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.containsInAnyOrder;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static solutions.tal.gallery.ArtType.PAINTING;
import static solutions.tal.gallery.ArtType.SCULPTURE;

/**
 * @author Thuan Luong
 */
public class GalleryTests {

    @Test
    public void art_can_be_added_to_gallery() {
        // given
        Gallery gallery = new SetBackedGalleryRepository();
        Art painting1 = new Art.Builder(PAINTING, "Painting1", "Artist1").build();
        gallery.addArt(painting1);

        // then
        assertThat(gallery.getAllArt(), hasSize(1));
    }

    @Test
    public void add_art_pieces_in_separate_threads() throws InterruptedException {
        // given
        int numOfThreads = 3;
        CountDownLatch latch = new CountDownLatch(numOfThreads);
        Gallery gallery = new SetBackedGalleryRepository();
        ExecutorService executor = Executors.newFixedThreadPool(numOfThreads);

        // when
        executor.submit(new GalleryWorker(gallery, latch, 3, 10));
        executor.submit(new GalleryWorker(gallery, latch, 1, 5));
        executor.submit(new GalleryWorker(gallery, latch, 2, 15));

        latch.await(2, TimeUnit.SECONDS);

        // then
        assertThat(gallery.getAllArt(), hasSize(30));

        executor.shutdown();
    }

    @Test
    public void delete_art_piece() {
        // given
        Gallery gallery = new SetBackedGalleryRepository();
        Art painting1 = new Art.Builder(PAINTING, "Painting1", "Artist1").build();
        gallery.addArt(painting1);

        // when
        gallery.deleteArt(painting1);

        // then
        assertThat(gallery.getAllArt(), is(empty()));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void attempt_to_modify_art_collection_external_to_gallery_throws_exception() {
        // given
        Gallery gallery = new SetBackedGalleryRepository();
        Art painting1 = new Art.Builder(PAINTING, "Painting1", "Artist1").build();
        gallery.addArt(painting1);

        // when
        Set<Art> arts = gallery.getAllArt();
        arts.remove(painting1);
    }

    @Test
    public void can_get_list_of_unique_artists_ordered_alphabetically() {
        // given
        Gallery gallery = new SetBackedGalleryRepository();
        Art painting1 = new Art.Builder(PAINTING, "Painting1", "Artist1").build();
        Art painting2 = new Art.Builder(PAINTING, "Painting2", "Artist1").build();
        Art sculpture = new Art.Builder(SCULPTURE, "Sculpture", "Artist2").build();
        gallery.addArt(sculpture);
        gallery.addArt(painting2);
        gallery.addArt(painting1);

        // then
        assertThat(gallery.getArtists(), contains("Artist1", "Artist2"));
    }

    @Test
    public void can_get_art_pieces_for_a_given_artist() {
        // given
        Gallery gallery = new SetBackedGalleryRepository();
        Art painting1 = new Art.Builder(PAINTING, "Painting1", "Artist1").build();
        Art painting2 = new Art.Builder(PAINTING, "Painting2", "Artist1").build();
        Art sculpture = new Art.Builder(SCULPTURE, "Sculpture", "Artist2").build();
        gallery.addArt(sculpture);
        gallery.addArt(painting2);
        gallery.addArt(painting1);

        // then
        assertThat(gallery.getArtByArtist("Artist1"), hasSize(2));
        assertThat(gallery.getArtByArtist("Artist2"), hasSize(1));
        assertThat(gallery.getArtByArtist("Unknown"), is(empty()));
    }

    @Test
    public void can_get_art_pieces_created_within_past_year() {
        // given
        Gallery gallery = new SetBackedGalleryRepository();
        LocalDateTime twoYearsAgo = LocalDateTime.now().minusYears(2);
        LocalDateTime oneMonthAgo = LocalDateTime.now().minusMonths(1);
        Art painting1 = new Art.Builder(PAINTING, "Painting1", "Artist1").createdOn(twoYearsAgo).build();
        Art painting2 = new Art.Builder(PAINTING, "Painting2", "Artist1").build();
        Art sculpture = new Art.Builder(SCULPTURE, "Sculpture", "Artist2").createdOn(oneMonthAgo).build();
        gallery.addArt(sculpture);
        gallery.addArt(painting2);
        gallery.addArt(painting1);

        // then
        assertThat(gallery.getAllArt(), hasSize(3));
        assertThat(gallery.getRecentArt(), hasSize(2));
    }

    @Test
    public void can_get_art_pieces_with_asking_price_in_range() {
        // given
        Gallery gallery = new SetBackedGalleryRepository();
        Art painting1 = new Art.Builder(PAINTING, "Painting1", "Artist1").withPrice(100.0).build();
        Art painting2 = new Art.Builder(PAINTING, "Painting2", "Artist1").build();
        Art sculpture = new Art.Builder(SCULPTURE, "Sculpture", "Artist2").withPrice(200.0).build();
        gallery.addArt(sculpture);
        gallery.addArt(painting2);
        gallery.addArt(painting1);

        // then
        assertThat(gallery.getArtByPrice(100.0, 50.0), contains(painting1));
        assertThat(gallery.getArtByPrice(200.0, 100.0), containsInAnyOrder(painting1, sculpture));
        assertThat(gallery.getArtByPrice(200.0, 100.1), contains(sculpture));
        assertThat(gallery.getArtByPrice(null, null), containsInAnyOrder(painting1, sculpture));
        assertThat(gallery.getArtByPrice(300.0, 201.0), is(empty()));
    }

    private static final class GalleryWorker implements Runnable {

        private final Gallery gallery;

        private final CountDownLatch latch;

        private final int id;

        private final int numberOfArtPieces;

        public GalleryWorker(Gallery gallery, CountDownLatch latch, int id, int numberOfArtPieces) {
            this.gallery = gallery;
            this.latch = latch;
            this.id = id;
            this.numberOfArtPieces = numberOfArtPieces;
        }

        @Override
        public void run() {
            try {
                for (int i=0; i<numberOfArtPieces; i++) {
                    Art art = new Art.Builder(PAINTING, format("Name", id, i), format("Artist", id, i)).build();
                    gallery.addArt(art);
                }
            } finally {
                latch.countDown();
            }
        }

        private static String format(String prefix, int id, int index) {
            return String.format("%s_%d_%d", prefix, id, index);
        }
    }
}
