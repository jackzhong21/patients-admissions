import { test, expect } from "@playwright/test";

test.describe("Admissions", () => {
  test("list page loads with table", async ({ page }) => {
    await page.goto("/admissions");
    await expect(page.locator("table")).toBeVisible();
    await expect(page.getByText("Patient Admissions")).toBeVisible();
  });

  test("redirects from home to /admissions", async ({ page }) => {
    await page.goto("/");
    await expect(page).toHaveURL("/admissions");
  });

  test("create regular admission", async ({ page }) => {
    await page.goto("/admissions/create");
    await page.getByTestId("name-input").fill("Test Patient");
    await page.getByTestId("birthday-input").fill("1990-01-15");

    // Select sex
    await page.getByTestId("sex-select").click();
    await page.getByRole("option", { name: "FEMALE" }).click();

    // Select category
    await page.getByTestId("category-select").click();
    await page.getByRole("option", { name: "INPATIENT" }).click();

    await page.getByTestId("submit-button").click();
    await expect(page).toHaveURL("/admissions");
  });

  test("future birthday prevents form submission", async ({ page }) => {
    await page.goto("/admissions/create");
    await page.getByTestId("name-input").fill("Future Patient");
    await page.getByTestId("birthday-input").fill("2099-01-01");
    await page.getByTestId("submit-button").click();

    await expect(
      page.getByText("Birthday cannot be in the future")
    ).toBeVisible();
    // Should still be on create page
    await expect(page).toHaveURL("/admissions/create");
  });

  test("delete admission removes row", async ({ page }) => {
    // First create one
    await page.goto("/admissions/create");
    await page.getByTestId("name-input").fill("To Be Deleted");
    await page.getByTestId("birthday-input").fill("1985-06-20");
    await page.getByTestId("sex-select").click();
    await page.getByRole("option", { name: "FEMALE" }).click();
    await page.getByTestId("category-select").click();
    await page.getByRole("option", { name: "NORMAL" }).click();
    await page.getByTestId("submit-button").click();
    await expect(page).toHaveURL("/admissions");

    // Now delete it
    const deleteButton = page.getByLabel("delete To Be Deleted").first();
    await deleteButton.click();
    await page.getByRole("button", { name: "Delete" }).click();

    await expect(page.getByText("To Be Deleted")).not.toBeVisible();
  });
});
