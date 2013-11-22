package driss


import org.assertj.core.api.Assertions.assertThat
import org.junit.Test as test
import fr.codestory.elevator.BuildingDimension

class SignalsTests {

    test public fun distanceToNearestFloor_with_two_positive_floor() {

        val signals = signals(BuildingDimension(0, 10), 0)

        signals.add(1, 1)
        signals.add(2, 1)

        assertThat(signals.distanceToNearestFloorFrom(0))!!.isEqualTo(1)
    }

    test public fun distanceToNearestFloor_with_ont_positive_floor() {

        val signals = signals(BuildingDimension(0, 10), 0)

        signals.add(2, 1)

        assertThat(signals.distanceToNearestFloorFrom(0))!!.isEqualTo(2)
    }

    test public fun distanceToNearestFloor_with_nothing_at_any_floor() {

        val signals = signals(BuildingDimension(0, 10), 0)

        assertThat(signals.distanceToNearestFloorFrom(0))!!.isEqualTo(0)
    }

    test public fun distanceToNearestFloor_with_a_negative_floor() {

        val signals = signals(BuildingDimension(-3, 10), 0)

        signals.add(-3, 1)

        assertThat(signals.distanceToNearestFloorFrom(0))!!.isEqualTo(3)
    }

    test public fun distanceToNearestFloor_with_a_negative_and_positive_floors() {

        val signals = signals(BuildingDimension(-3, 10), 0)

        signals.add(-3, 1)
        signals.add(8, 1)

        assertThat(signals.distanceToNearestFloorFrom(1))!!.isEqualTo(4)
    }


    test public fun nearest_should_give_the_nearest_negative_floor() {

        val signals = signals(BuildingDimension(-3, 10), 0)

        signals.add(-3, 1)
        signals.add(8, 1)

        assertThat(signals.nearestFloorFrom(1))!!.isEqualTo(-3)
    }

    test public fun nearest_should_give_the_nearest_downside_on_equality() {

        val signals = signals(BuildingDimension(0, 10), 0)

        signals.add(1, 1)
        signals.add(3, 1)

        assertThat(signals.nearestFloorFrom(2))!!.isEqualTo(1)
    }

    test public fun nearest_should_give_the_nearest_positive_floor() {

        val signals = signals(BuildingDimension(0, 10), 0)

        signals.add(3, 1)
        signals.add(8, 1)

        assertThat(signals.nearestFloorFrom(1))!!.isEqualTo(3)
    }

    test public fun nearest_should_give_itself_when_no_floor_registered() {

        val signals = signals(BuildingDimension(0, 10), 0)

        assertThat(signals.nearestFloorFrom(1))!!.isEqualTo(1)
    }
}
