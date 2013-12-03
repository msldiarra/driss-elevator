package driss


import org.assertj.core.api.Assertions.assertThat
import org.junit.Test as test
import fr.codestory.elevator.BuildingDimension
import fr.codestory.elevator.Elevator.Side

class SignalsTests {

    test fun signals_should_compute_number_of_floors(): Unit =
            with(signals(BuildingDimension(-1, 1), Unit.VALUE)) {

                assertThat(this.count())!!.isEqualTo(3)
                Unit.VALUE
            }


    test public fun distanceToNearestFloor_with_two_positive_floor() {

        val signals = signals(BuildingDimension(0, 10), Go(0))

        signals.add(1, Go(1))
        signals.add(2, Go(1))

        assertThat(signals.distanceToNearestFloorFrom(0))!!.isEqualTo(1)
    }

    test public fun distanceToNearestFloor_with_ont_positive_floor() {

        val signals = signals(BuildingDimension(0, 10), Go(0))

        signals.add(2, Go(1))

        assertThat(signals.distanceToNearestFloorFrom(0))!!.isEqualTo(2)
    }

    test public fun distanceToNearestFloor_with_nothing_at_any_floor() {

        val signals = signals(BuildingDimension(0, 10), Go(0))

        assertThat(signals.distanceToNearestFloorFrom(0))!!.isEqualTo(0)
    }

    test public fun distanceToNearestFloor_with_a_negative_floor() {

        val signals = signals(BuildingDimension(-3, 10), Go(0))

        signals.add(-3, Go(1))

        assertThat(signals.distanceToNearestFloorFrom(0))!!.isEqualTo(3)
    }

    test public fun distanceToNearestFloor_with_a_negative_and_positive_floors() {

        val signals = signals(BuildingDimension(-3, 10), Go(0))

        signals.add(-3, Go(1))
        signals.add(8, Go(1))

        assertThat(signals.distanceToNearestFloorFrom(1))!!.isEqualTo(4)
    }


    test public fun nearest_should_give_the_nearest_negative_floor() {

        assertThat(nearestFloorFrom(1, listOf(-3, 8)))!!.isEqualTo(-3)
    }

    test public fun nearest_should_give_the_nearest_downside_on_equality() {

        assertThat(nearestFloorFrom(2, listOf(1, 3)))!!.isEqualTo(1)
    }

    test public fun nearest_should_give_the_nearest_positive_floor() {

        assertThat(nearestFloorFrom(1, listOf(3, 8)))!!.isEqualTo(3)
    }

    test public fun nearest_should_give_itself_when_no_floor_registered() {

        assertThat(nearestFloorFrom(1, listOf()))!!.isEqualTo(1)
    }


    test fun going_should_give_calls_in_the_given_way_and_floor() {

        with(signals(
                BuildingDimension(0, 4),
                emptyImmutableArrayList as MutableList<Call>
        )) {

            add(1, arrayListOf(Call(Side.UP, 1), Call(Side.UP, 1), Call(Side.DOWN, 1)))

            assertThat(at(1).going(Side.UP))!!.hasSize(2)
            assertThat(at(1).going(Side.DOWN))!!.hasSize(1)

            assertThat(at(0).going(Side.UP))!!.hasSize(0)
        }
    }

    test fun going() {

        with(listOf(Call(Side.DOWN, 1), Call(Side.UP))) {

            assertThat(going(Side.UP))!!.hasSize(1)
            assertThat(going(Side.DOWN))!!.hasSize(1)
        }
    }
}
