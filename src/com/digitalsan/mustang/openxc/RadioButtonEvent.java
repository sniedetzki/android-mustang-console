package com.digitalsan.mustang.openxc;

import java.util.Locale;

import com.openxc.measurements.BaseMeasurement;
import com.openxc.units.State;


public class RadioButtonEvent extends BaseMeasurement<State<RadioButtonEvent.ButtonId>> 
{


	/**
	 * A ButtonEvent represents a button press, release or hold on the vehicle HMI.
	 *
	 * This measurement is only valid when used asynchronously, much like any other
	 * key or button event in Java. An application registers to receive button
	 * events, and decides what to do based on the returned ButtonId and
	 * ButtonAction.
	 *
	 * If this measurement is retrieved synchronously, it will return the last
	 * button event received - this is probably not what you want.
	 *
	 * TODO This is by far the ugliest measurement because it has to incorporate two
	 * different values instead of the usual one. Is this implementation saying
	 * something about the architecture in general, or is it just an edge case we
	 * need to ignore and let live?
	 */

	public final static String ID = "radio_button_event";

		/**
		 * The ButtonId is the direction of a button within a single control
		 * cluster.
		 */
		public enum ButtonId {
			LEFT,
			RIGHT,
			UP,
			DOWN,
			OK,
			PRESET0,
			PRESET1,
			PRESET2,
			PRESET3,
			PRESET4,
			PRESET5,
			PRESET6,
			PRESET7,
			PRESET8,
			PRESET9,
			VOLUMEUP,
			VOLUMEDOWN,
			TUNEUP,
			TUNEDOWN,
			RADIO,
			CD,
			SIRIUS,
			AUX,
			EJECT,
			LOAD,
			DIRECT,
			SCAN,
			TEXT,
			CATFOLDER,
			MENU,
			SOUND,
			CLOCK,
			PHONE
		}

		/**
		 * The ButtonAction is the specific event that occurred.
		 */
		public enum ButtonAction {
			PRESSED,
			RELEASED,
		}

		public RadioButtonEvent(State<ButtonId> value,
				State<ButtonAction> event) {
			super(value, event);
		}

		public RadioButtonEvent(ButtonId value, ButtonAction event) {
			this(new State<ButtonId>(value), new State<ButtonAction>(event));
		}

		public RadioButtonEvent(String value, String event) {
			this(ButtonId.valueOf(value.toUpperCase(Locale.US)),
					ButtonAction.valueOf(event.toUpperCase(Locale.US)));
		}

		@SuppressWarnings("unchecked")
		@Override
		public State<ButtonAction> getEvent() {
			return (State<ButtonAction>) super.getEvent();
		}

		@Override
		public String getSerializedEvent() {
			return getEvent().enumValue().toString();
		}

		@Override
		public String getSerializedValue() {
			return getValue().enumValue().toString();
		}

		@Override
		public String getGenericName() {
			return ID;
		}
	}
