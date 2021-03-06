package info.rueth.fpucalculator.usecases;

import android.app.Application;

import info.rueth.fpucalculator.domain.model.Food;
import info.rueth.fpucalculator.domain.repository.FoodDataRepository;
import info.rueth.fpucalculator.presentation.viewmodels.FoodViewModel;

public class FoodEdit implements FoodUseCase {
    private FoodDataRepository repository;

    public FoodEdit(Application application) {
        repository = FoodDataRepository.getInstance(application);
    }

    public void execute(FoodViewModel viewModel) {
        int id = viewModel.getId();
        fillViewModelData(viewModel, id);
    }

    private void fillViewModelData(FoodViewModel viewModel, int id) {
        Food data = repository.getFoodByID(id);
        if (data != null) {
            viewModel.setName(data.getName());
            viewModel.setFavorite(data.isFavorite());
            viewModel.setCaloriesPer100g(data.getCaloriesPer100g());
            viewModel.setCarbsPer100g(data.getCarbsPer100g());
            viewModel.setAmountSmall(data.getAmountSmall());
            viewModel.setAmountMedium(data.getAmountMedium());
            viewModel.setAmountLarge(data.getAmountLarge());
            viewModel.setCommentSmall(data.getCommentSmall());
            viewModel.setCommentMedium(data.getCommentMedium());
            viewModel.setCommentLarge(data.getCommentLarge());
        }
    }
}
