<?php

include_once("../admin/global.php");
include_once(SITE_ROOT."teacher_ideas/referrer.php");

include_once(SITE_ROOT."page_templates/SitePage.php");

class EditContributionPage extends SitePage {

    function print_javascript_error() {
        print <<<EOT
            <h2>Error in submission</h2>

            <p>Sorry, there was as error with your submission..</p>

            <p>JavaScript is not enabled.</p>
            <p>You must have JavaScript enabled to submit information to PhET.  For directions on how to check this, <a href="../tech_support/support-javascript.php  ">go here</a>.</p>

EOT;
    }

    function update_contribution($contribution) {
        $username = auth_get_username();
        $contributor = contributor_get_contributor_by_username($username);

        // Only allow team members to change these fields:
        if ($contributor['contributor_is_team_member'] != 1) {
            unset($contribution['contribution_from_phet']);
            unset($contribution['contribution_is_gold_star']);
            if (isset($_REQUEST['contributor_id']))
            $contribution['contributor_id'] = $_REQUEST['contributor_id'];
        }
        else {
            $contribution['contributor_id'] = $_REQUEST['contributor_id'];
            if (isset($_REQUEST['new_contributor_id'])) {
                $contribution['contributor_id'] = $_REQUEST['new_contributor_id'];
            }
        }

        if (!isset($contribution['contribution_id']) || $contribution['contribution_id'] == -1) {
            // Updating a contribution that does not exist. First, create it:
            $contribution['contribution_id'] = contribution_add_new_contribution(
                $contribution['contribution_title'],
                $contribution['contributor_id']
            );

            //$GLOBALS['contribution_id'] = $contribution['contribution_id'];
        }

        contribution_update_contribution($contribution);

        $contribution_id = $contribution['contribution_id'];

        contribution_delete_all_multiselect_associations('contribution_level',   $contribution_id);
        contribution_delete_all_multiselect_associations('contribution_type',    $contribution_id);
        contribution_delete_all_multiselect_associations('contribution_subject', $contribution_id);

        contribution_unassociate_contribution_with_all_simulations($contribution_id);

        // Establish multiselect associations (level, subject, type):
        $files_to_keep = contribution_establish_multiselect_associations_from_script_params($contribution_id);

        $standards_compliance = generate_encoded_checkbox_string('standards');

        contribution_update_contribution(
            array(
                'contribution_id'                   => $contribution_id,
                'contribution_standards_compliance' => $standards_compliance
            )
        );

        // Automatically approve 'edited' submission:
        contribution_set_approved($contribution_id, true);

        // Handle the temporary table element
        db_delete_row("temporary_partial_contribution_track", array("contribution_id" => $contribution_id));

        // Handle files:

        // First, delete all files we aren't supposed to keep:
        contribution_delete_all_files_not_in_list($contribution_id, $files_to_keep);

        // Second, add all new files:
        contribution_add_all_form_files_to_contribution($contribution_id);
    }

    function handle_action($action) {
        if ($action == 'update') {
            // First, make sure that the page has the special tag that suggests JavaScript
            // is enabled.  If JavaScript is not enabled, this special hidden input will
            // not be created.  Of course, this is circumventable, but this eliminates
            // most of the non-malicious people.
            if (!$this->javascript_submit_is_valid()) {
                $this->print_javascript_error();
                return false;
            }

            $contribution = gather_script_params_into_array('contribution_');

            $this->update_contribution($contribution);

            cache_clear(BROWSE_CACHE);
            return true;
        }
        else {
            // Throw?
            return false;
        }
    }

    function print_success() {
        print <<<EOT
            <h2>Update Success</h2>

            <p><strong>Thank you! The contribution has been successfully updated.</strong></p>

            <p><a href="{$this->referrer}">continue</a></p>

EOT;
    }

    function update() {
        $result = parent::update();
        if (!$result) {

        }

        ob_start();

        if (!isset($_REQUEST['contribution_id'])) {
            print <<<EOT
            <p>You must specify a contribution to edit.  Try going to the <a href="manage-contributions.php">Manage Contributions</a> page, and select a activity to edit.</p>

EOT;
        }
        else if ((contribution_get_contribution_by_id($_REQUEST['contribution_id']) === false) &&
                ($_REQUEST['contribution_id'] != -1)){
            print <<<EOT
            <p>Invalid contribution id specified.  Try going to the <a href="manage-contributions.php">Manage Contributions</a> page, and select a activity to edit.</p>

EOT;
        }
        else {
            if ($this->authentication_level < SP_AUTHLEVEL_USER) {
                $this->add_javascript_header_script("disable_not_always_enabled_form_elements();");
            }

            $contribution_id = $_REQUEST['contribution_id'];
            $username = auth_get_username();
            $contributor = contributor_get_contributor_by_username($username);

            if (contribution_can_contributor_manage_contribution($contributor["contributor_id"], $contribution_id)) {
                if (isset($_REQUEST['action'])) {
                    $success = $this->handle_action($_REQUEST['action']);

                    if ($success) {
                        $this->referrer = "../teacher_ideas/manage-contributions.php";
                        $this->meta_refresh("../teacher_ideas/manage-contributions.php", 3);
                        $this->print_success();
                    }
                    else {
                        contribution_print_full_edit_form($contribution_id, "../teacher_ideas/edit-contribution.php", $this->referrer, "Update", $this);
                    }
                }
                else {
                    contribution_print_full_edit_form($contribution_id, "../teacher_ideas/edit-contribution.php", $this->referrer, "Update", $this);
                }
            }
            else {
                print <<<EOT
            <p>You do not have permission to edit this activity.  Try going to the <a href="manage-contributions.php">Manage Contributions</a> page, and select a different activity to edit.</p>

EOT;
            }
        }

        //contribution_print_full_edit_form2(-1, '../teacher_ideas/edit-contribution.php', '../teacher_ideas/edit-contribution.php', 'Submit', $this);
        $this->add_content(ob_get_clean());
        return true;
    }

    function render_content() {
        $result = parent::render_content();
        if (!$result) {
            return BasePage::render_content();//$result;
        }
    }
}

$page = new EditContributionPage("Edit an Activity", NAV_TEACHER_IDEAS, get_referrer("../teacher_ideas/edit-contribution.php"), SP_AUTHLEVEL_USER);
$page->update();
$page->render();

?>