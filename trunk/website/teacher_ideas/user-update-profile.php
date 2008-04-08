<?php

include_once("../admin/global.php");
include_once(SITE_ROOT."page_templates/SitePage.php");

class UpdateUserProfile extends SitePage {

    function update() {
        $result = parent::update();
        if (!$result) {
            return $result;
        }

        if (!auth_user_validated()) {
            return;
        }

        $contributor_id = contributor_get_id_from_contributor_username(auth_get_username());

        // HACK: check on passwords matching,
        //   encrypt password in the $_REQUEST var, and
        //   uset it if it is not changing
        $this->password_failure = false;
        $pass1 = (isset($_REQUEST["new_contributor_password"])) ? trim($_REQUEST["new_contributor_password"]) : "";
        $pass2 = (isset($_REQUEST["new_contributor_password2"])) ? trim($_REQUEST["new_contributor_password2"]) : "";

        if ((strlen($pass1) > 0) || (strlen($pass2) > 0)) {
            if ($pass1 == $pass2) {
                $pass1 = encrypt_password($_REQUEST["new_contributor_password"]);
            }
            else {
                $this->password_failure = true;
                return false;
            }
        }

        if (strlen($pass1) > 0) {
            $_REQUEST["contributor_password"] = $pass1;
        }
        else {
            if (isset($_REQUEST["contributor_password"])) {
                unset($_REQUEST["contributor_password"]);
            }

        }

        if (isset($_REQUEST["new_contributor_password"])) {
            unset($_REQUEST["new_contributor_password"]);
        }

        if (isset($_REQUEST["new_contributor_password2"])) {
            unset($_REQUEST["new_contributor_password2"]);
        }

        $this->success = db_update_table('contributor', gather_script_params_into_array('contributor_'), 'contributor_id', $contributor_id);

        if ((strlen($pass1) > 0) && ($this->success)) {
            // Make sure the cookie password is updated
            cookie_var_store("contributor_password_hash", $pass1);
        }

        //$this->meta_refresh(SITE_ROOT."teacher_ideas/user-edit-profile.php", 3);
        return true;
    }

    function render_content() {
        $result = parent::render_content();
        if (!$result) {
            return $result;
        }

        if (isset($this->success) && $this->success) {
            print <<<EOT
        <p>Your profile has been successfully updated!</p>

EOT;
            return;
        }
        else if (isset($this->password_failure) && $this->password_failure) {
            print <<<EOT
        <p>There was an error with your passwords.  They must match and they may not be blank.</p>

EOT;
        }
        else {
            print <<<EOT
        <p>There was an error, please go back and try again.</p>

EOT;
        }
    }
}

$page = new UpdateUserProfile("Update User Profile", NAV_TEACHER_IDEAS, null, SP_AUTHLEVEL_USER);
$page->update();
$page->render();

?>