/**
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package main.java.pt.lighthouselabs.obd.commands.engine;

import pt.lighthouselabs.obd.commands.ObdCommand;
import pt.lighthouselabs.obd.enums.AvailableCommandNames;

/**
 * Displays the current engine revolutions per minute (RPM).
 */
public class EngineRPMObdCommand extends ObdCommand {

  private int rpm = -1;

  /**
   * Default ctor.
   */
  public EngineRPMObdCommand() {
    super("01 0C");
  }

  /**
   * Copy ctor.
   * 
   * @param other
   */
  public EngineRPMObdCommand(EngineRPMObdCommand other) {
    super(other);
  }

  @Override
  protected void performCalculations() {
    // ignore first two bytes [41 0C] of the response
    rpm = (buffer.get(2) * 256 + buffer.get(3)) / 4;
  }

  /**
   * @return the engine RPM per minute
   */
  @Override
  public String getFormattedResult() {
    return String.format("%d%s", rpm, " RPM");
  }

  @Override
  public String getName() {
    return AvailableCommandNames.ENGINE_RPM.getValue();
  }

  public int getRPM() {
    return rpm;
  }

}
