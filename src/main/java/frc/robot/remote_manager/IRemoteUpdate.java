package frc.robot.remote_manager;

public interface IRemoteUpdate {
    /**
     * method for an {@code Object} to react to a new state, either manually
     * or by the Python client. implementation is not prescriptive but
     * implementing classes <a href="https://datatracker.ietf.org/doc/html/rfc2119">MUST</a>
     * safely cease responding to any/all commands if {@code state == 0} and
     * safely return to normal operation if {@code state == 1}
     * @param state
     */
    public void updateNumericState(Integer state);

    /**
     * method that <a href="https://datatracker.ietf.org/doc/html/rfc2119">SHOULD</a> return
     * the current integer state of an {@code Object}. i can't force that upon you but just
     * do it, okay?
     * @return the current state of the {@code Object}
     */
    public Integer getNumericState();
}
