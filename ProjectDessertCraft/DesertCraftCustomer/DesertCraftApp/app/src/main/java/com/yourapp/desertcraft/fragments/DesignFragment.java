package com.yourapp.desertcraft.fragments;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.yourapp.desertcraft.R;
import com.yourapp.desertcraft.databinding.FragmentDesignBinding;

import java.io.IOException;
import java.util.UUID;

import de.hdodenhof.circleimageview.CircleImageView;


public class DesignFragment extends Fragment {
    private FragmentDesignBinding binding;
    private int currentColor;

    private final ActivityResultLauncher<String> filePickerLauncher =
            registerForActivityResult(new ActivityResultContracts.GetContent(),
                    this::handleSelectedFile);

    private final ActivityResultLauncher<String> requestPermissionLauncher =
            registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
                if (isGranted) {
                    saveImage();
                } else {
                    Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show();
                }
            });


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDesignBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.imgGallery.setOnClickListener(view1 -> openFilePicker());

        binding.imgBrush.setOnClickListener(view1 -> {
            if (binding.cardSeekBar.getVisibility() == View.VISIBLE) {
                binding.cardSeekBar.setVisibility(View.GONE);
            } else {
                binding.cardSeekBar.setVisibility(View.VISIBLE);
            }
        });

        binding.seekBarBrushSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                binding.paintView.setBrushSize(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });

        binding.imgColorPicker.setOnClickListener(view1 -> {
            if (binding.cardColors.getVisibility() == View.VISIBLE) {
                binding.cardColors.setVisibility(View.GONE);
            } else {
                binding.cardColors.setVisibility(View.VISIBLE);
            }
        });

        setUpColorPicker(view);

        binding.imgUndo.setOnClickListener(v -> binding.paintView.undo());

        binding.imgRedo.setOnClickListener(v -> binding.paintView.redo());

        binding.imgDownload.setOnClickListener(v -> checkStoragePermission());
    }

    private void openFilePicker() {
        filePickerLauncher.launch("image/*");
    }

    private void handleSelectedFile(Uri uri) {
        if (uri != null) {
            String fileType = requireActivity().getContentResolver().getType(uri);
            if (fileType != null) {
                if (fileType.startsWith("image/")) {
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), uri);
                        binding.paintView.setBackgroundImage(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Toast.makeText(requireContext(), "Unsupported file type", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    private void setUpColorPicker(View view) {
        int[] colorButtons = {
                R.id.color0,R.id.color1, R.id.color2, R.id.color3, R.id.color4, R.id.color5, R.id.color6,
                R.id.color7, R.id.color8, R.id.color9, R.id.color10, R.id.color11, R.id.color12, R.id.color13,
                R.id.color14, R.id.color15, R.id.color16, R.id.color17, R.id.color18, R.id.color19, R.id.color20
        };

        int[] colors = {
                ContextCompat.getColor(requireContext(), R.color.color0),
                ContextCompat.getColor(requireContext(), R.color.color1),
                ContextCompat.getColor(requireContext(), R.color.color2),
                ContextCompat.getColor(requireContext(), R.color.color3),
                ContextCompat.getColor(requireContext(), R.color.color4),
                ContextCompat.getColor(requireContext(), R.color.color5),
                ContextCompat.getColor(requireContext(), R.color.color6),
                ContextCompat.getColor(requireContext(), R.color.color7),
                ContextCompat.getColor(requireContext(), R.color.color8),
                ContextCompat.getColor(requireContext(), R.color.color9),
                ContextCompat.getColor(requireContext(), R.color.color10),
                ContextCompat.getColor(requireContext(), R.color.color11),
                ContextCompat.getColor(requireContext(), R.color.color12),
                ContextCompat.getColor(requireContext(), R.color.color13),
                ContextCompat.getColor(requireContext(), R.color.color14),
                ContextCompat.getColor(requireContext(), R.color.color15),
                ContextCompat.getColor(requireContext(), R.color.color16),
                ContextCompat.getColor(requireContext(), R.color.color17),
                ContextCompat.getColor(requireContext(), R.color.color18),
                ContextCompat.getColor(requireContext(), R.color.color19),
                ContextCompat.getColor(requireContext(), R.color.color20)
        };

        currentColor = colors[0];
        binding.paintView.setBrushColor(currentColor);

        for (int i = 0; i < colorButtons.length; i++) {
            final int color = colors[i];
            CircleImageView colorButton = view.findViewById(colorButtons[i]);
            colorButton.setOnClickListener(v -> {
                currentColor = color;
                binding.paintView.setBrushColor(color);
                binding.cardColors.setVisibility(View.GONE);
            });
        }
    }

    private void checkStoragePermission() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            saveImage();
        } else {
            requestPermissionLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
    }

    private void saveImage() {
        Bitmap bitmap = binding.paintView.getBitmap();

        String savedImageURL = MediaStore.Images.Media.insertImage(
                requireActivity().getContentResolver(),
                bitmap,
                UUID.randomUUID().toString() + ".png",
                "drawing"
        );

        if (savedImageURL != null) {
            Toast.makeText(getActivity(), "Image saved to gallery", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(getActivity(), "Failed to save image", Toast.LENGTH_SHORT).show();
        }
    }


}