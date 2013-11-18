package driss


import org.assertj.core.api.Assertions.assertThat
import org.junit.Test as test
import driss.destinations

class DestinationsTests {

    test public fun distanceToNearestFloor_with_two_positive_floor() {

        val destinations = destinations(Unit.VALUE)

        destinations.add(1, Unit.VALUE)
        destinations.add(2, Unit.VALUE)

        assertThat(destinations.distanceToNearestFloorFrom(0))!!.isEqualTo(1)
    }

    test public fun distanceToNearestFloor_with_ont_positive_floor() {

        val destinations = destinations(Unit.VALUE)

        destinations.add(2, Unit.VALUE)

        assertThat(destinations.distanceToNearestFloorFrom(0))!!.isEqualTo(2)
    }

    test public fun distanceToNearestFloor_with_nothing_at_any_floor() {

        val destinations = destinations(Unit.VALUE)

        assertThat(destinations.distanceToNearestFloorFrom(0))!!.isEqualTo(0)
    }

    test public fun distanceToNearestFloor_with_a_negative_floor() {

        val destinations = destinations(Unit.VALUE)

        destinations.add(-3, Unit.VALUE)

        assertThat(destinations.distanceToNearestFloorFrom(0))!!.isEqualTo(3)
    }

    test public fun distanceToNearestFloor_with_a_negative_and_positive_floors() {

        val destinations = destinations(Unit.VALUE)

        destinations.add(-3, Unit.VALUE)
        destinations.add(8, Unit.VALUE)

        assertThat(destinations.distanceToNearestFloorFrom(1))!!.isEqualTo(4)
    }


    test public fun nearest_should_give_the_nearest_negative_floor() {

        val destinations = destinations(Unit.VALUE)

        destinations.add(-3, Unit.VALUE)
        destinations.add(8, Unit.VALUE)

        assertThat(destinations.nearestFloorFrom(1))!!.isEqualTo(-3)
    }

    test public fun nearest_should_give_the_nearest_downside_on_equality() {

        val destinations = destinations(Unit.VALUE)

        destinations.add(1, Unit.VALUE)
        destinations.add(3, Unit.VALUE)

        assertThat(destinations.nearestFloorFrom(2))!!.isEqualTo(1)
    }

    test public fun nearest_should_give_the_nearest_positive_floor() {

        val destinations = destinations(Unit.VALUE)

        destinations.add(3, Unit.VALUE)
        destinations.add(8, Unit.VALUE)

        assertThat(destinations.nearestFloorFrom(1))!!.isEqualTo(3)
    }

    test public fun nearest_should_give_itself_when_no_floor_registered() {

        val destinations = destinations(Unit.VALUE)

        assertThat(destinations.nearestFloorFrom(1))!!.isEqualTo(1)
    }


}
