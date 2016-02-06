/*
 * Copyright (c) 2016 Wout van Helvoirt [w.van.helvoirt@st.hanze.nl] & Lonneke Scheffer [l.scheffer@st.hanze.nl]
 * All rights reserved.
 *
 */
package nl.bioinf.lscheffer_wvanhelvoirt.insanemodel.sessionListeners;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;
import nl.bioinf.lscheffer_wvanhelvoirt.insanemodel.configuration.ConfigurationPaths;

/**
 *
 * @author Lonneke Scheffer
 */
public class FileControlSessionListener implements HttpSessionListener {

    @Override
    public void sessionCreated(HttpSessionEvent se) {
    }

    @Override
    public void sessionDestroyed(HttpSessionEvent se) {
        try {
            // Try to delete any associated input- and outputfiles
            Files.deleteIfExists(Paths.get(ConfigurationPaths.getAbsoluteInFilePath(se.getSession().getId())));
            Files.deleteIfExists(Paths.get(ConfigurationPaths.getAbsoluteOutFilePath(se.getSession().getId())));
        } catch (IOException ex) {

            // DOE HIER IETS??
            //Logger.getLogger(FileControlSessionListener.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
