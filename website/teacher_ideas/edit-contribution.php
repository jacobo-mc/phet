<?php

    include_once("../admin/global.php");

    include_once(SITE_ROOT."teacher_ideas/user-login.php");  
    
    include_once(SITE_ROOT."admin/contrib-utils.php");    
    include_once(SITE_ROOT."admin/site-utils.php");   
    include_once(SITE_ROOT."admin/web-utils.php");
    include_once(SITE_ROOT."teacher_ideas/referrer.php");  
    
    function update_contribution($contribution) {
        contribution_update_contribution($contribution);
        
        $contribution_id = $contribution['contribution_id'];
        
        contribution_delete_all_multiselect_associations('contribution_level',   $contribution_id);
        contribution_delete_all_multiselect_associations('contribution_type',    $contribution_id);
        contribution_delete_all_multiselect_associations('contribution_subject', $contribution_id);

        contribution_unassociate_contribution_with_all_simulations($contribution_id);
        
        // Now have to process multiselect controls:
        foreach($_REQUEST as $key => $value) {
            $matches = array();
            
            if (is_multiple_selection_control("$key")) {
                contribution_create_multiselect_association($contribution_id, $key, $value);
            }
            else if (preg_match('/sim_id_([0-9]+)/i', "$key", $matches) == 1) {
                $sim_id = $matches[1];
                
                contribution_associate_contribution_with_simulation($contribution_id, $sim_id);
            }
        }
        
        $standards_compliance = generate_encoded_checkbox_string('standards');
        
        contribution_update_contribution(
            array(
                'contribution_id'                   => $contribution_id,
                'contribution_standards_compliance' => $standards_compliance
            )
        );
    }
    
    function print_content_no_permission() {
        global $referrer;
        
        print <<<EOT
            <h1>Permission Error</h2>
            
            <p>You do not have permission to edit the specified contribution.</p>
EOT;

        print "<p><a href=\"$referrer\">cancel</a></p>";
    }

    function print_content() {
        global $referrer, $contribution_id;
        
        /*
    
        Array ( 
            [contribution_authors]              => John A. De Goes
            [contribution_authors_organization] => University of Colorado
            [contribution_contact_email]        => degoes@colorado.edu
            [contribution_title]                => Another contribution 4
            [contribution_keywords]             =>
            [contribution_desc]                 => 
            [contribution_duration]             => 30 
            [contribution_answers_included]     => 0 
            [contribution_id]                   => 34
        )
    
        */
    
        ?>
    
        <h1>Edit Contribution</h1>
    
        <?php
    
        contribution_print_full_edit_form($contribution_id, "edit-contribution.php", $referrer);  
            
        print "<p><a href=\"$referrer\">cancel</a></p>";
    }
    
    if (isset($_REQUEST['sim_id'])) {
        $sim_id = $_REQUEST['sim_id'];
    }
    
    $contribution_id = $_REQUEST['contribution_id'];
    
    if (isset($_REQUEST['action'])) {
        handle_action($_REQUEST['action']);
    }
    
    $contribution = gather_script_params_into_array('contribution_');   
    
    if (contribution_can_contributor_manage_contribution($contributor_id, $contribution_id)) {    
        update_contribution($contribution);
        
        print_site_page('print_content', 3);
    }
    else {
        print_site_page('print_content_no_permission', 3);
    }

?>