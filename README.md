# INSANE M↻DEL #

---------------------

### What is this repository for? ###

* Authors: Lonneke Scheffer & Wout van Helvoirt
* Version: 1.0.0
* This web application makes use of Insane (included in this repository), a Python program from the [Martini](http://md.chem.rug.nl/) package, to create various 3D models. Models created by Insane can be modified and viewed from within the web browser to suit your needs.

### How do I get set up? ###

* This web application requires at least [Java 8](https://www.oracle.com/downloads/index.html) to function.
* [Apache Tomcat](http://tomcat.apache.org/download-80.cgi) version 8.0.28 has been used to create a local host server. Newer versions might work too, but have not been tested.
* Download this version of [JSON simple](https://bitbucket.org/penuts7644/insaneweb/downloads/json-simple-1.1.1.jar).
* There are a few paths that must exist in order for Insane Model to work. To temporarily store the user input files you need the directory '<your home folder>/insanewebfiles/insanewebfiles'. Also, your 'NetBeansProjects' folder must be inside your home folder. If you want to use different paths (for instance: you don't want to have the 'insanewebfiles' or 'NetBeansProjects' folders inside your home folder, or you don't use NetBeans), all you have to do is change the paths in the java class 'ConfigurationPaths'. (More information can be found in the javadoc of this class.)
* The complete distribution including Javadoc can be downloaded [here](https://bitbucket.org/penuts7644/insaneweb/downloads/insane_web_dist.zip).

### How do I use this web application? ###

The tool is easy to navigate. When first visiting the site through server (or localhost), your greeted with an overview including a small description. The menu and Jmol viewer are shown when scrolling down, or by clicking the arrow button.

On the left side of the window is the input field in which all necessary values can be given. Each panel (Gridsize, Membrane, Protein and Solvent) can be opened or closed by clicking on the panel title. By clicking on the 'Advanced options' button, extra input fields are given for the corresponding panel. These 'Advanced options' are not required. All input field shown on default are required, altho default values are used by the web application.

The submit button as well as Jmol viewer are located on the right side of the window. When pressing 'Create View/Update View', all given input values will be processed and the created model will be shown in the viewer. This process could take some time depending on the amount processing power available and the complexity of the model. The submit button will be disabled when pressed or when a input value is not correct. A download button will be shown at the right side of the 'Create View/Update View' button. Pressing the button lets you download the output .gro file.

When an error occurs, it may still be possible for the application to display output. These warnings are displayed on the right side of the 'Create View/Update View' button.

---------------------

**Note 1: JavaScript must be enabled in your web browser.**

**Note 2: Make sure that your display has at least 900 pixels in width.**

**Note 3: This web application works best using the browsers Firefox, Iceweasel or Safari. Chrome and Chromium are not recommended since they do not work very well together with JSmol. JSmol tends to be very slow when using Chrome/Chromium, and these browsers sometimes refresh the page when updating the JSmol view.**

**Note 4: The Firefox add-on "Privacy Badger" must be disabled completely for Jmol to work. [See this page.](http://wiki.jmol.org/index.php/Compatibility)**