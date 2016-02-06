/*
 * Copyright (c) 2016 Wout van Helvoirt [w.van.helvoirt@st.hanze.nl] & Lonneke Scheffer [l.scheffer@st.hanze.nl]
 * All rights reserved.
 *
 */

/*
 * Javascript for loading the Jmol in the correct container.
 */
$(document).ready(function() {
    var jmol_applet_insane;
    Jmol._isAsync = false;
    Jmol._document = null;

    /*
     * Get remaining height to be used for the Jmol container.
     * -2 is for the border of container. Below are parameters for Jmol.
     */
    var windows_height = $('#fixeddiv').height() - $('#controldiv').outerHeight(true) - 2;
    var jmol_applet_insane_parameters = {
        color: '#F5F5F5',
        width: '100%',
        height: windows_height,
        j2sPath: 'js/j2s',
        use: 'HTML5',
        disableInitialConsole: true,
        disableJ2SLoadMonitor: true,
        debug: false,
        addSelectionOptions: false,
        serverURL: 'http://chemapps.stolaf.edu/jmol/jsmol/php/jsmol.php',
        src: null,
        allowjavascript: true
    };

    /*
     * Load the applet into to correct container. Also disable the menu.
     */
    jmol_applet_insane = Jmol.getApplet('jmol_applet_insane', jmol_applet_insane_parameters);
    $('#jmolViewer').html(jmol_applet_insane._code);
    Jmol.script(jmol_applet_insane, 'set disablePopupMenu TRUE');
});